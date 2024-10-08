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
    private val updateFolderNameUseCase: UpdateFolderNameUseCase,
    private val deleteFolderUseCase: DeleteFolderUseCase
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
                getAllDocumentsUseCase().collect { rootContent ->
                    val contentWithDummy = addDummyDataIfEmpty(rootContent)
                    _documents.value = contentWithDummy
                    _currentFolderName.value = "문서"
                    currentFolder = FolderNode(-1L, "문서", null)
                }
            } catch (e: Exception) {
                Log.e("DocumentsViewModel", "Error loading documents", e)
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
                getSubItemsUseCase(folderId).collect { subItems ->
                    _documents.value = subItems
                    _currentFolderName.value = folderName
                    currentFolder = FolderNode(folderId, folderName, currentFolder)
                }
            } catch (e: Exception) {
                Log.e("DocumentsViewModel", "Error loading sub-items", e)
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
}