package com.iguana.documents

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.http.HttpException
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iguana.domain.model.FolderContent
import com.iguana.domain.model.FolderContentItem
import com.iguana.domain.model.RecentFile
import com.iguana.domain.usecase.*
import com.iguana.notetaking.NotetakingActivity
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
    private val updateDocumentNameUseCase: UpdateDocumentNameUseCase,
    private val saveRecentFileUsecase: SaveRecentFileUsecase
) : ViewModel() {

    private val _documents = MutableStateFlow<List<FolderContentItem>>(emptyList())
    val documents: StateFlow<List<FolderContentItem>> = _documents.asStateFlow()

    private val _currentFolderName = MutableStateFlow("문서")
    val currentFolderName: StateFlow<String> = _currentFolderName

    var currentFolder: FolderNode? = null
    var currentFolderId: Long = -1L

    private val _documentItems = MutableStateFlow<List<DocumentItem>>(emptyList())
    val documentItems: StateFlow<List<DocumentItem>> = _documentItems

    private val _currentFolderCount = MutableStateFlow(Pair(0, 0)) // 폴더 수와 파일 수 저장
    val currentFolderCount: StateFlow<Pair<Int, Int>> = _currentFolderCount.asStateFlow()

    private var currentParentFolderId: Long = -1L

    init {
        loadAllDocuments()
    }

    fun openFile(recentFile: RecentFile, context: Context) {
        viewModelScope.launch {
            saveRecentFileUsecase.invoke(recentFile.id, recentFile.fileName, recentFile.fileUri)
        }
        val intent = Intent(context, NotetakingActivity::class.java).apply {
            putExtra("PDF_URI", recentFile.fileUri)
            putExtra("PDF_TITLE", recentFile.fileName)
            putExtra("DOCUMENT_ID", recentFile.id)
        }
        context.startActivity(intent)
    }


    fun loadAllDocuments() {
        viewModelScope.launch {
            try {
                val rootContent = getAllDocumentsUseCase()
                currentFolderId = -1L
                _documents.value = rootContent.items
                _currentFolderName.value = "문서"
                currentFolder = null
                updateUI(rootContent)
                Log.d(
                    "DocumentsViewModel",
                    "루트 폴더 문서 로딩 완료 - 폴더: ${rootContent.folderCount}, 문서: ${rootContent.documentCount}"
                )
            } catch (e: Exception) {
                Log.e("DocumentsViewModel", "문서 로딩 중 오류 발생", e)
            }
        }
    }

    fun loadFolderContents(folderId: Long, folderName: String, newParent: FolderNode? = null) {
        viewModelScope.launch {
            try {
                val folderContent = getFolderContentsUseCase(folderId)

                _documents.value = folderContent.items
                _currentFolderName.value = folderName

                currentFolder = FolderNode(
                    id = folderId,
                    name = folderName,
                    parent = if (folderId != -1L) newParent else null,
                    folderCount = folderContent.folderCount,
                    fileCount = folderContent.documentCount
                )

                currentFolderId = folderId
                updateUI(folderContent)
                Log.d(
                    "DocumentsViewModel",
                    "폴더 이동 - 현재: $folderName, 부모: ${currentFolder?.parent?.name}"
                )
            } catch (e: Exception) {
                Log.e("DocumentsViewModel", "폴더 내용 로딩 중 예외 발생", e)
            }
        }
    }

    fun navigateUp() {
        when {
            currentFolderId == -1L -> return  // 이미 루트 폴더면 아무것도 하지 않음
            currentFolder?.parent == null -> {
                // 부모가 없으면 루트로 이동
                loadAllDocuments()
            }

            else -> {
                // 부모 폴더가 있으면 해당 폴더로 이동
                currentFolder?.parent?.let { parentFolder ->
                    currentFolderId = parentFolder.id
                    _currentFolderName.value = parentFolder.name
                    loadFolderContents(parentFolder.id, parentFolder.name, parentFolder.parent)
                }
            }
        }
    }

    fun createFolder(folderName: String) {
        viewModelScope.launch {
            createFolderUsecase.execute(currentFolderId, folderName)
            // 폴더 생성 후 현재 폴더 내용을 새로고침
            refreshCurrentFolder()
        }
    }

    fun updateFolderName(folderId: Long, newName: String) {
        viewModelScope.launch {
            updateFolderNameUseCase(folderId, newName)
            // 폴더 이름 변경 후 현재 폴더 내용을 새로고침
            refreshCurrentFolder()
        }
    }

    fun deleteFolder(folderId: Long) {
        viewModelScope.launch {
            deleteFolderUseCase(folderId)
            // 폴더 삭제 후 현재 폴더 내용을 새로고침
            refreshCurrentFolder()
        }
    }

    fun deleteFile(fileId: Long) {
        viewModelScope.launch {
            deleteFileUseCase(currentFolderId, fileId)
            refreshCurrentFolder()
            Log.d("DocumentsViewModel", "파일이 성공적으로 삭제되었습니다.")
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
                        val document = saveFileInRemoteUsecase.execute(currentFolderId, internalUri, fileName)
                        // 3. 현재 폴더 내용을 새로고침하여 정렬된 상태로 표시
                        refreshCurrentFolder()
                        // 4. Room 데이터베이스에 저장 (내부 URI 사용)
                        if (document != null) {
                            document.url?.let {
                                saveRecentFileUsecase.invoke(
                                    document.id,
                                    fileName,
                                    it
                                )
                            }
                            // 4. 선택된 파일을 NotetakingActivity로 전달
                            val intent = Intent(context, NotetakingActivity::class.java).apply {
                                putExtra("PDF_URI", document.url)
                                putExtra("PDF_TITLE", fileName)
                                putExtra("DOCUMENT_ID", document.id)
                            }
                            context.startActivity(intent)
                        }
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
            cursor.use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    result =
                        cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
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
        val items = folderContent.items.map { item ->
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
                    isBookmarked = false,
                    url = item.url!!
                )

                else -> DocumentItem.PdfItem(  // 기본값도 PdfItem으로
                    id = item.id,
                    title = item.name,
                    timestamp = item.updatedAt,
                    isBookmarked = false,
                    url = item.url!!
                )
            }
        }
        _documentItems.value = items
        _currentFolderCount.value = Pair(folderContent.folderCount, folderContent.documentCount) // 폴더/파일 수 업데이트

        Log.d(
            "DocumentsViewModel",
            "UI 업데이트 - 전체: ${items.size}, 폴더: ${folderContent.folderCount}, 문서: ${folderContent.documentCount}"
        )
    }

    // 현재 폴더의 내용을 새로고침하는 함수 추가
    fun refreshCurrentFolder() {
        viewModelScope.launch {
            try {
                if (currentFolderId == -1L) {
                    val content = getAllDocumentsUseCase()
                    _documents.value = content.items
                    updateUI(content)
                } else {
                    val content = getFolderContentsUseCase(currentFolderId)
                    _documents.value = content.items
                    updateUI(content)
                }
            } catch (e: Exception) {
                Log.e("DocumentsViewModel", "폴더 내용 새로고침 실패", e)
            }
        }
    }

    fun updateDocumentName(folderId: Long, documentId: Long, newName: String) {
        viewModelScope.launch {
            updateDocumentNameUseCase(folderId, documentId, newName)
            // 문서 제목 변경 후 현재 폴더 내용을 새로고침
            refreshCurrentFolder()
        }
    }

    // 일반 폴더 진입 시 사용하는 함수
    fun onFolderClick(folderId: Long, folderName: String) {
        loadFolderContents(folderId, folderName, currentFolder)
    }
}

data class FolderNode(
    val id: Long,
    val name: String,
    val parent: FolderNode?,
    val folderCount: Int = 0,
    val fileCount: Int = 0
)