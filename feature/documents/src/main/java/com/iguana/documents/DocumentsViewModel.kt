package com.iguana.documents

import android.content.Context
import android.net.Uri
import android.net.http.HttpException
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iguana.domain.model.FolderContent
import com.iguana.domain.model.FolderContentItem
import com.iguana.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.logging.Logger
import javax.inject.Inject

@HiltViewModel
class DocumentsViewModel @Inject constructor(
    private val getAllDocumentsUseCase: GetAllDocumentsUseCase,
    private val getFolderContentsUseCase: GetFolderContentsUseCase,
    private val createFolderUsecase: CreateFolderUseCase,
    private val updateFolderNameUseCase: UpdateFolderNameUseCase,
    private val deleteFolderUseCase: DeleteFolderUseCase,
    private val deleteFileUseCase: DeleteFileUseCase,
    private val saveFileInLocalUsecase: SaveFileInLocalUsecase,
    private val saveFileInRemoteUsecase: SaveFileInRemoteUsecase,
    private val updateDocumentNameUseCase: UpdateDocumentNameUseCase
) : ViewModel() {

    private val _documents = MutableStateFlow<List<FolderContentItem>>(emptyList())
    val documents: StateFlow<List<FolderContentItem>> = _documents.asStateFlow()

    private val _currentFolderName = MutableStateFlow("문서")
    val currentFolderName: StateFlow<String> = _currentFolderName

    private var currentFolder: FolderNode? = null
    var currentFolderId: Long = -1L

    private val _documentItems = MutableStateFlow<List<DocumentItem>>(emptyList())
    val documentItems: StateFlow<List<DocumentItem>> = _documentItems

    init {
        loadAllDocuments()
    }

    fun loadAllDocuments() {
        viewModelScope.launch {
            try {
                val result = getAllDocumentsUseCase()
                result.onSuccess { rootContent ->
                    currentFolderId = -1L
                    _documents.value = rootContent
                    _currentFolderName.value = "문서"
                    currentFolder = null
                    updateUI(rootContent)
                }.onFailure { e ->
                    Log.e("DocumentsViewModel", "문서 로딩 중 오류 발생", e)
                }
            } catch (e: Exception) {
                Log.e("DocumentsViewModel", "문서 로딩 중 오류 발생", e)
            }
        }
    }

    fun loadFolderContents(folderId: Long, folderName: String) {
        currentFolderId = folderId
        viewModelScope.launch {
            try {
                val result = getFolderContentsUseCase(
                    folderId = folderId,
                    page = 0,
                    size = 20,
                    sortBy = "updatedAt",
                    sortDirection = "DESC"
                )
                result.onSuccess { folderContent ->
                    _documents.value = folderContent
                    _currentFolderName.value = folderName
                    currentFolder = FolderNode(folderId, folderName, currentFolder)
                    updateUI(folderContent)
                }.onFailure { e ->
                    Log.e("DocumentsViewModel", "폴더 내용 로딩 중 오류 발생", e)
                }
            } catch (e: Exception) {
                Log.e("DocumentsViewModel", "폴더 내용 로딩 중 예외 발생", e)
            }
        }
    }

    fun navigateUp() {
        if (currentFolder == null) {
            return
        }
        
        currentFolder?.parent?.let { parentFolder ->
            currentFolderId = parentFolder.id
            loadFolderContents(parentFolder.id, parentFolder.name)
            currentFolder = parentFolder
        } ?: run {
            currentFolderId = -1L
            loadAllDocuments()
        }
    }

    fun createFolder(folderName: String) {
        viewModelScope.launch {
            createFolderUsecase.execute(currentFolderId, folderName).onSuccess { newFolder ->
                // 폴더 생성 후 현재 폴더 내용을 새로고침
                refreshCurrentFolder()
            }.onFailure { error ->
                Log.e("DocumentsViewModel", "폴더 생성 실패", error)
            }
        }
    }

    fun updateFolderName(folderId: Long, newName: String) {
        viewModelScope.launch {
            updateFolderNameUseCase(folderId, newName).onSuccess { updatedFolder ->
                // 폴더 이름 변경 후 현재 폴더 내용을 새로고침
                refreshCurrentFolder()
            }.onFailure { error ->
                Log.e("DocumentsViewModel", "폴더 이름 변경 실패", error)
            }
        }
    }

    fun deleteFolder(folderId: Long) {
        viewModelScope.launch {
            deleteFolderUseCase(folderId).onSuccess {
                // 폴더 삭제 후 현재 폴더 내용을 새로고침
                refreshCurrentFolder()
            }.onFailure { error ->
                Log.e("DocumentsViewModel", "폴더 삭제 실패", error)
            }
        }
    }

    fun deleteFile(fileId: Long) {
        viewModelScope.launch {
            deleteFileUseCase(currentFolderId, fileId).onSuccess {
                refreshCurrentFolder()
                Log.d("DocumentsViewModel", "파일이 성공적으로 삭제되었습니다.")
            }.onFailure { error ->
                Log.e("DocumentsViewModel", "파일 삭제 실패", error)
            }
        }
    }

    fun uploadPdf(uri: Uri?, context: Context) {
        if (uri != null) {
            val fileName = getFileName(context, uri)
            
            viewModelScope.launch {
                try {
                    // 1. 로컬에 파일 저장
                    val internalUri = saveFileInLocalUsecase.execute(uri, fileName)
                    
                    if (internalUri != null) {
                        // 2. 서버에 파일 업로드 (현재 폴더 ID 사용)
                        val result = saveFileInRemoteUsecase.execute(currentFolderId, internalUri, fileName)
                        result.onSuccess { document ->
                            // 3. 현재 폴더 내용을 새로고침하여 정렬된 상태로 표시
                            refreshCurrentFolder()
                        }.onFailure {
                            Toast.makeText(context, "파일 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "파일 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "파일 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getFileName(context: Context, uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result ?: "unknown"
    }

    private fun updateUI(folderContent: FolderContent) {
        val items = folderContent.map { item ->
            when (item.type.uppercase()) {
                "FOLDER" -> DocumentItem.FolderItem(
                    id = item.id,
                    name = item.name,
                    fileCount = item.totalElements,
                    isBookmarked = false
                )
                "DOCUMENT" -> DocumentItem.PdfItem(  // DOCUMENT 타입을 PdfItem으로 매핑
                    id = item.id,
                    title = item.name,
                    timestamp = item.updatedAt,
                    isBookmarked = false
                )
                else -> DocumentItem.PdfItem(  // 기본값도 PdfItem으로
                    id = item.id,
                    title = item.name,
                    timestamp = item.updatedAt,
                    isBookmarked = false
                )
            }
        }
        _documentItems.value = items
        
        Log.d("DocumentsViewModel", "UI 업데이트 - 전체: ${items.size}, 폴더: ${items.count { it is DocumentItem.FolderItem }}, 문서: ${items.count { it is DocumentItem.PdfItem }}")
    }

    // 현재 폴더의 내용을 새로고침하는 함수 추가
    fun refreshCurrentFolder() {
        viewModelScope.launch {
            try {
                if (currentFolderId == -1L) {
                    // 루트 폴더인 경우
                    getAllDocumentsUseCase().onSuccess { content ->
                        _documents.value = content
                        updateUI(content)
                    }
                } else {
                    // 특정 폴더인 경우
                    getFolderContentsUseCase(
                        folderId = currentFolderId,
                        page = 0,
                        size = 20,
                        sortBy = "updatedAt",
                        sortDirection = "DESC"
                    ).onSuccess { content ->
                        _documents.value = content
                        updateUI(content)
                    }
                }
            } catch (e: Exception) {
                Log.e("DocumentsViewModel", "폴더 내용 새로고침 실패", e)
            }
        }
    }

    fun updateDocumentName(folderId: Long, documentId: Long, newName: String) {
        viewModelScope.launch {
            updateDocumentNameUseCase(folderId, documentId, newName).onSuccess { updatedDocument ->
                // 문서 제목 변경 후 현재 폴더 내용을 새로고침
                refreshCurrentFolder()
            }.onFailure { error ->
                Log.e("DocumentsViewModel", "문서 제목 변경 실패", error)
            }
        }
    }
}

data class FolderNode(
    val id: Long,
    val name: String,
    val parent: FolderNode?
)