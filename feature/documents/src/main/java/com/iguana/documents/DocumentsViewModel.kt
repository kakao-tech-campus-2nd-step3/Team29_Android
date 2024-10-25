package com.iguana.documents

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iguana.domain.model.FolderContent
import com.iguana.domain.model.FolderContentItem
import com.iguana.domain.usecase.CreateFolderUseCase
import com.iguana.domain.usecase.DeleteFolderUseCase
import com.iguana.domain.usecase.GetAllDocumentsUseCase
import com.iguana.domain.usecase.GetSubItemsUseCase
import com.iguana.domain.usecase.UpdateFolderNameUseCase
import com.iguana.domain.usecase.DeleteFileUseCase
import com.iguana.domain.usecase.UpdateDocumentNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Stack
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class DocumentsViewModel @Inject constructor(
    private val getAllDocumentsUseCase: GetAllDocumentsUseCase,
    private val getSubItemsUseCase: GetSubItemsUseCase,
    private val createFolderUsecase: CreateFolderUseCase,
    private val updateDocumentNameUseCase: UpdateDocumentNameUseCase,
    private val updateFolderNameUseCase: UpdateFolderNameUseCase,
    private val deleteFolderUseCase: DeleteFolderUseCase,
    private val deleteFileUseCase: DeleteFileUseCase
) : ViewModel() {

    private val _documents = MutableStateFlow<FolderContent?>(null)
    val documents: StateFlow<FolderContent?> = _documents.asStateFlow()

    private val _currentFolderName = MutableStateFlow("문서")
    val currentFolderName: StateFlow<String> = _currentFolderName

    private data class FolderNode(val id: Long, val name: String, val parent: FolderNode?)
    private var currentFolder: FolderNode = FolderNode(-1L, "문서", null)

    var currentFolderId: Long = 0L

    fun loadAllDocuments() {
        viewModelScope.launch {
            try {
                val result = getAllDocumentsUseCase()
                result.onSuccess { rootContent ->
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

    private fun addDummyDataIfEmpty(content: FolderContent): FolderContent {
        if (content.content.isEmpty()) {
            val dummyFolder = createDummyFolder()
            val dummyPdf = createDummyPdf()
            return content.copy(content = listOf(dummyFolder, dummyPdf))
        }
        return content
    }

    private fun createDummyFolder(): FolderContentItem {
        return FolderContentItem(
            id = 9999L,
            name = "테스트 폴더",
            type = "FOLDER",
            totalElements = 0,
            updatedAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        )
    }

    private fun createDummyPdf(): FolderContentItem {
        return FolderContentItem(
            id = 9998L,
            name = "테스트 PDF.pdf",
            type = "PDF",
            totalElements = 1,
            updatedAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        )
    }

    fun loadSubItems(folderId: Long, folderName: String) {
        currentFolderId = folderId
        viewModelScope.launch {
            try {
                val result = getSubItemsUseCase(folderId)
                result.onSuccess { subItems ->
                    val contentWithDummy = addDummyDataIfEmpty(subItems)
                    _documents.value = contentWithDummy
                    _currentFolderName.value = folderName
                    currentFolder = FolderNode(folderId, folderName, currentFolder)
                }.onFailure { e ->
                    Log.e("DocumentsViewModel", "하위 항목 로딩 중 오류 발생", e)
                }
            } catch (e: Exception) {
                Log.e("DocumentsViewModel", "하위 항목 로딩 중 예외 발생", e)
            }
        }
    }

    fun navigateUp() {
        currentFolder.parent?.let { parentFolder ->
            if (parentFolder.id == -1L) {
                loadAllDocuments()
            } else {
                loadSubItems(parentFolder.id, parentFolder.name)
            }
            currentFolder = parentFolder
        } ?: loadAllDocuments()
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
                val updatedContent = _documents.value?.content?.toMutableList() ?: mutableListOf()
                updatedContent.add(newFolderItem)
                _documents.value = _documents.value?.copy(content = updatedContent)
                Log.d("DocumentsViewModel", "Documents updated: ${updatedContent.size} items")
            }.onFailure {
                Log.e("DocumentsViewModel", "Error creating folder", it)
            }
        }
    }

    fun updateFolderName(folderId: Long, newName: String) {
        viewModelScope.launch {
            updateFolderNameUseCase(folderId, newName).onSuccess { updatedFolder ->
                val currentContent = _documents.value?.content ?: emptyList()
                val updatedContent = currentContent.map { item ->
                    if (item.id == folderId) updatedFolder else item
                }
                _documents.value = _documents.value?.copy(content = updatedContent)
            }.onFailure {
                // 에러 처리
                Log.e("DocumentsViewModel", "폴더 이름 변경 실패", it)
            }
        }
    }

    fun updateDocumentName(documentId: Long, newName: String) {
        viewModelScope.launch {
            updateDocumentNameUseCase(documentId, newName).onSuccess { updatedDocument ->
                _documents.value?.let { currentContent ->
                    val updatedContent = currentContent.content.map { item ->
                        if (item.id == documentId) {
                            item.copy(name = newName)
                        } else {
                            item
                        }
                    }
                    _documents.value = currentContent.copy(content = updatedContent)
                }
                Log.d("DocumentsViewModel", "문서 이름이 성공적으로 수정되었습니다.")
            }.onFailure { error ->
                Log.e("DocumentsViewModel", "문서 이름 수정 실패: ${error.message}", error)
            }
        }
    }

    fun deleteFolder(folderId: Long) {
        viewModelScope.launch {
            deleteFolderUseCase(folderId).onSuccess {
                // 폴더 삭제 성공 처리
                val updatedContent = _documents.value?.content?.filter { it.id != folderId }
                _documents.value = _documents.value?.copy(content = updatedContent ?: emptyList())
                
                // 성공 로그
                Log.d("DocumentsViewModel", "폴더가 성공적으로 삭제되었습니다.")
            }.onFailure { error ->
                // 에러 로그
                Log.e("DocumentsViewModel", "폴더 삭제 실패: ${error.message}", error)
            }
        }
    }

    fun deleteFile(fileId: Long) {
        viewModelScope.launch {
            deleteFileUseCase(fileId).onSuccess {
                // 파일 삭제 성공 처리
                val updatedContent = _documents.value?.content?.filter { it.id != fileId }
                _documents.value = _documents.value?.copy(content = updatedContent ?: emptyList())
                
                // 성공 로그
                Log.d("DocumentsViewModel", "파일이 성공적으로 삭제되었습니다.")
            }.onFailure { error ->
                // 에러 로그
                Log.e("DocumentsViewModel", "파일 삭제 실패: ${error.message}", error)
            }
        }
    }
}