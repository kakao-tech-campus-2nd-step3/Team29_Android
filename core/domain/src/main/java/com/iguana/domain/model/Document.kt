package com.iguana.domain.model

data class Document(
    val id: Long,
    val folderId: Long?,
    val name: String,
    val url: String?,
    val pageCount: Int?,
    val updatedAt: String
)

data class FolderContent(
    val content: List<FolderContentItem>,
    val totalElements: Int,
    val currentPage: Int,
    val totalPages: Int,
    val sortBy: String,
    val sortDirection: String
)

data class FolderContentItem(
    val type: String,
    val id: Long,
    val name: String,
    val updatedAt: String,
    val totalElements: Int
)

data class Folder(
    val id: Long,
    val parentId: Long,
    val name: String
)

data class MoveItemsRequest(
    val documentIds: List<Long>,
    val folderIds: List<Long>,
    val destinationFolderId: Long
)

sealed class DocumentItem {
    data class FolderItem(
        val id: Long,
        val name: String,
        val fileCount: Int,
        val isBookmarked: Boolean
    ) : DocumentItem()

    data class PdfItem(
        val id: Long,
        val title: String,
        val timestamp: String,
        val isBookmarked: Boolean
    ) : DocumentItem()
}