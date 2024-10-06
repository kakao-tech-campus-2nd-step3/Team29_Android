package com.iguana.documents

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iguana.domain.model.FolderContent
import com.iguana.domain.model.FolderContentItem
import com.iguana.domain.repository.DocumentsRepository
import com.iguana.domain.usecase.GetAllDocumentsUseCase
import com.iguana.domain.usecase.GetSubItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Stack
import javax.inject.Inject

@HiltViewModel
class DocumentsViewModel @Inject constructor(
    private val getAllDocumentsUseCase: GetAllDocumentsUseCase,
    private val getSubItemsUseCase: GetSubItemsUseCase
) : ViewModel() {

    private val _documents = MutableStateFlow<FolderContent?>(null)
    val documents: StateFlow<FolderContent?> = _documents

    private val _currentFolderName = MutableStateFlow("문서")
    val currentFolderName: StateFlow<String> = _currentFolderName

    private data class FolderNode(val id: Long, val name: String, val parent: FolderNode?)
    private var currentFolder: FolderNode = FolderNode(-1L, "문서", null)

    fun loadAllDocuments() {
        viewModelScope.launch {
            val rootContent = FolderContent(
                content = listOf(
                    FolderContentItem(
                        id = 1L,
                        name = "예시 폴더 1",
                        type = "FOLDER",
                        updatedAt = "2023-05-30T10:00:00Z",
                        totalElements = 3
                    ),
                    FolderContentItem(
                        id = 2L,
                        name = "예시 폴더 2",
                        type = "FOLDER",
                        updatedAt = "2023-05-31T11:00:00Z",
                        totalElements = 2
                    ),
                    FolderContentItem(
                        id = 3L,
                        name = "예시 폴더 3",
                        type = "FOLDER",
                        updatedAt = "2023-05-29T09:00:00Z",
                        totalElements = 1
                    )
                ),
                totalElements = 3,
                currentPage = 0,
                sortBy = "updatedAt",
                sortDirection = "ASC",
                totalPages = 1
            )
            _documents.value = rootContent
            _currentFolderName.value = "문서"
            currentFolder = FolderNode(-1L, "문서", null)
        }
    }

    fun loadSubItems(folderId: Long, folderName: String) {
        viewModelScope.launch {
            val subItems = when (folderId) {
                1L -> listOf(
                    FolderContentItem(
                        id = 4L,
                        name = "하위 폴더 1-1",
                        type = "FOLDER",
                        updatedAt = "2023-05-30T11:00:00Z",
                        totalElements = 0
                    ),
                    FolderContentItem(
                        id = 5L,
                        name = "문서1.pdf",
                        type = "PDF",
                        updatedAt = "2023-05-30T12:00:00Z",
                        totalElements = 0
                    )
                )
                2L -> listOf(
                    FolderContentItem(
                        id = 6L,
                        name = "하위 폴더 2-1",
                        type = "FOLDER",
                        updatedAt = "2023-05-31T10:00:00Z",
                        totalElements = 0
                    )
                )
                3L -> listOf(
                    FolderContentItem(
                        id = 7L,
                        name = "문서3-1.pdf",
                        type = "PDF",
                        updatedAt = "2023-05-29T14:00:00Z",
                        totalElements = 0
                    ),
                    FolderContentItem(
                        id = 8L,
                        name = "문서3-2.pdf",
                        type = "PDF",
                        updatedAt = "2023-05-29T15:00:00Z",
                        totalElements = 0
                    )
                )
                else -> emptyList()
            }
            val folderContent = FolderContent(
                content = subItems,
                totalElements = subItems.size,
                currentPage = 0,
                sortBy = "updatedAt",
                sortDirection = "ASC",
                totalPages = 1
            )
            _documents.value = folderContent
            _currentFolderName.value = folderName
            currentFolder = FolderNode(folderId, folderName, currentFolder)
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
}