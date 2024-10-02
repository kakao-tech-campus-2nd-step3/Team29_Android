package com.iguana.data.remote.model

data class DocumentDto(
    val id: Long,
    val folderId: Long?,
    val name: String,
    val url: String?,
    val pageCount: Int?,
    val updatedAt: String
)

data class FolderContentDto(
    val type: String,
    val id: Long,
    val name: String,
    val updatedAt: String,
    val totalElements: Int
)

data class FolderContentResponseDto(
    val content: List<FolderContentDto>,
    val totalElements: Int,
    val currentPage: Int,
    val totalPages: Int,
    val sortBy: String,
    val sortDirection: String
)

data class CreateFolderRequestDto(
    val name: String
)

data class CreateFolderResponseDto(
    val id: Long,
    val parentId: Long,
    val name: String
)

data class MoveFolderRequestDto(
    val documentIds: List<Long>,
    val folderIds: List<Long>,
    val destinationFolderId: Long
)