package com.iguana.domain.model

data class Document(
    val id: Long,
    val folderId: Long?,
    val name: String,
    val url: String?,
    val pageCount: Int?,
    val updatedAt: String
)

// FolderContent 데이터 클래스 정의
data class FolderContent(
    val items: List<FolderContentItem>,
    val folderCount: Int,
    val documentCount: Int
)

// FolderContentItem 리스트에 대한 타입 별칭
typealias FolderContentList = List<FolderContentItem>

data class FolderContentItem(
    val type: String,
    val id: Long,
    val name: String,
    val updatedAt: String,
    val totalElements: Int,
    val url: String?
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
