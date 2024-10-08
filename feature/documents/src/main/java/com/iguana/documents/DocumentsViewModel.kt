package com.iguana.documents

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iguana.domain.model.FolderContent
import com.iguana.domain.model.FolderContentItem
import com.iguana.domain.usecase.CreateFolderUseCase
import com.iguana.domain.usecase.GetAllDocumentsUseCase
import com.iguana.domain.usecase.GetSubItemsUseCase
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
    private val createFolderUsecase: CreateFolderUseCase
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
                    _documents.value = rootContent
                    _currentFolderName.value = "문서"
                    currentFolder = FolderNode(-1L, "문서", null)
                }
            } catch (e: Exception) {
                Log.e("DocumentsViewModel", "Error loading documents", e)
            }
        }
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
}