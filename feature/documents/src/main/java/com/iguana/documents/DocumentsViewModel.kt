package com.iguana.documents

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iguana.domain.model.FolderContent
import com.iguana.domain.model.FolderContentItem
import com.iguana.domain.usecase.CreateFolderUseCase
import com.iguana.domain.usecase.DeleteFolderUseCase
import com.iguana.domain.usecase.GetAllDocumentsUseCase
import com.iguana.domain.usecase.GetFolderContentsUseCase
import com.iguana.domain.usecase.UpdateFolderNameUseCase
import com.iguana.domain.usecase.DeleteFileUseCase
import com.iguana.domain.usecase.UpdateDocumentNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class DocumentsViewModel @Inject constructor(
    private val getAllDocumentsUseCase: GetAllDocumentsUseCase,
    private val getFolderContentsUseCase: GetFolderContentsUseCase,
    private val createFolderUsecase: CreateFolderUseCase,
    private val updateDocumentNameUseCase: UpdateDocumentNameUseCase,
    private val updateFolderNameUseCase: UpdateFolderNameUseCase,
    private val deleteFolderUseCase: DeleteFolderUseCase,
    private val deleteFileUseCase: DeleteFileUseCase
) : ViewModel() {

    private val _documents = MutableStateFlow<List<FolderContentItem>>(emptyList())
    val documents: StateFlow<List<FolderContentItem>> = _documents.asStateFlow()

    private val _currentFolderName = MutableStateFlow("문서")
    val currentFolderName: StateFlow<String> = _currentFolderName

    private data class FolderNode(val id: Long, val name: String, val parent: FolderNode?)
    private var currentFolder: FolderNode = FolderNode(-1L, "문서", null)

    var currentFolderId: Long = -1L

    fun loadAllDocuments() {
        viewModelScope.launch {
            try {
                val result = getAllDocumentsUseCase()
                result.onSuccess { rootContent ->
                    currentFolderId = -1L
                    val contentWithDummy = addDummyDataIfEmpty(rootContent)
                    _documents.value = contentWithDummy
                    _currentFolderName.value = "문서"
                    currentFolder = FolderNode(-1L, "문서", null)
                }.onFailure { e ->
                    Log.e("DocumentsViewModel", "문서 로딩 중 오류 발생", e)
                }
            } catch (e: Exception) {
                Log.e("DocumentsViewModel", "문서 로딩 중 오류 발생", e)
            }
        }
    }

    private fun addDummyDataIfEmpty(content: List<FolderContentItem>): List<FolderContentItem> {
        if (content.isEmpty()) {
            return when (currentFolderId) {
                -1L -> listOf(
                    createDummyFolder(),
                    createDummyPdf("테스트 PDF.pdf")
                )
                9999L -> listOf(
                    createDummySubFolder(),
                    createDummyPdf("테스트2 PDF.pdf")
                )
                else -> emptyList()
            }
        }
        return content
    }

    private fun createDummyFolder(): FolderContentItem {
        return FolderContentItem(
            id = 9999L,
            name = "테스트 폴더",
            type = "FOLDER",
            totalElements = 2,
            updatedAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        )
    }

    private fun createDummySubFolder(): FolderContentItem {
        return FolderContentItem(
            id = 9998L,
            name = "테스트2 폴더",
            type = "FOLDER",
            totalElements = 0,
            updatedAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        )
    }

    private fun createDummyPdf(name: String): FolderContentItem {
        return FolderContentItem(
            id = 9997L,
            name = name,
            type = "PDF",
            totalElements = 1,
            updatedAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        )
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
                }.onFailure { e ->
                    Log.e("DocumentsViewModel", "폴더 내용 로딩 중 오류 발생", e)
                }
            } catch (e: Exception) {
                Log.e("DocumentsViewModel", "폴더 내용 로딩 중 예외 발생", e)
            }
        }
    }

    fun navigateUp() {
        currentFolder.parent?.let { parentFolder ->
            currentFolderId = parentFolder.id
            if (parentFolder.id == -1L) {
                loadAllDocuments()
            } else {
                loadFolderContents(parentFolder.id, parentFolder.name)
            }
            currentFolder = parentFolder
        } ?: run {
            currentFolderId = -1L
            loadAllDocuments()
        }
    }

    fun createFolder(parentFolderId: Long, folderName: String) {
        viewModelScope.launch {
            val result = createFolderUsecase.execute(parentFolderId, folderName)
            result.onSuccess { folder ->
                Log.d("DocumentsViewModel", "Folder created: ${folder.name}")
                val newFolderItem = FolderContentItem(
                    id = folder.id,
                    name = folder.name,
                    type = "FOLDER",
                    totalElements = 0,
                    updatedAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
                )
                val updatedContent = _documents.value.toMutableList()
                updatedContent.add(newFolderItem)
                _documents.value = updatedContent
                Log.d("DocumentsViewModel", "Documents updated: ${updatedContent.size} items")
            }.onFailure {
                Log.e("DocumentsViewModel", "Error creating folder", it)
            }
        }
    }

    fun updateFolderName(folderId: Long, newName: String) {
        viewModelScope.launch {
            updateFolderNameUseCase(folderId, newName).onSuccess { updatedFolder ->
                val updatedContent = _documents.value.map { item ->
                    if (item.id == folderId) updatedFolder else item
                }
                _documents.value = updatedContent
            }.onFailure {
                Log.e("DocumentsViewModel", "폴더 이름 변경 실패", it)
            }
        }
    }

    fun updateDocumentName(documentId: Long, newName: String) {
        viewModelScope.launch {
            updateDocumentNameUseCase(documentId, newName).onSuccess { updatedDocument ->
                val updatedContent = _documents.value.map { item ->
                    if (item.id == documentId) {
                        item.copy(name = newName)
                    } else {
                        item
                    }
                }
                _documents.value = updatedContent
            }.onFailure { error ->
                Log.e("DocumentsViewModel", "문서 이름 수정 실패: ${error.message}", error)
            }
        }
    }

    fun deleteFolder(folderId: Long) {
        viewModelScope.launch {
            deleteFolderUseCase(folderId).onSuccess {
                _documents.value = _documents.value.filter { it.id != folderId }
                Log.d("DocumentsViewModel", "폴더가 성공적으로 삭제되었습니다.")
            }.onFailure { error ->
                Log.e("DocumentsViewModel", "폴더 삭제 실패: ${error.message}", error)
            }
        }
    }

    fun deleteFile(fileId: Long) {
        viewModelScope.launch {
            deleteFileUseCase(fileId).onSuccess {
                _documents.value = _documents.value.filter { it.id != fileId }
                Log.d("DocumentsViewModel", "파일이 성공적으로 삭제되었습니다.")
            }.onFailure { error ->
                Log.e("DocumentsViewModel", "파일 삭제 실패: ${error.message}", error)
            }
        }
    }
}