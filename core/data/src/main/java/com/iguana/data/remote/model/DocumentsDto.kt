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
    val type: String?,
    val id: Long,
    val name: String,
    val updatedAt: String?,
    val totalElements: Int
)

data class FolderContentResponseDto(
    val response: ResponseDto?,
    val folderAndDocumentResponseType: String
)

data class ResponseDto(
    val id: Long,
    val name: String,
    val updatedAt: String?,
    val totalElements: Int = -1
)

data class CreateFolderRequestDto(
    val name: String,
    val parentFolderId: Long?
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

data class FolderContentItemDto(
    val type: String,
    val id: Long,
    val name: String,
    val updatedAt: String,
    val totalElements: Int
)

data class UpdateFolderNameRequestDto(
    val name: String
)