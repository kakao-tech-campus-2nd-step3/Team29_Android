package com.iguana.data.remote.model

data class GetFolderContentResponseDto(
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

data class UpdateContentNameRequestDto(
    val name: String
)

data class UpdateDocumentNameResponseDto(
    val id: Long,
    val name: String,
    val url: String
)

data class CreateDocumentResponseDto(
    val id: Long,
    val name: String,
    val url: String
)

data class GetDocumentsResponseDto(
    val id: Long,
    val name: String,
    val url: String
)

data class MoveFolderResponseDto(
    val id: Long,
    val name: String
)

data class UpdateFolderNameResponseDto(
    val id: Long,
    val parentId: Long,
    val name: String
)