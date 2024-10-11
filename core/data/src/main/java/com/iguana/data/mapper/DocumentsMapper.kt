package com.iguana.data.mapper

import com.iguana.data.remote.model.*
import com.iguana.domain.model.*

// DTO to Domain
fun DocumentDto.toDomain() = Document(id, folderId, name, url, pageCount, updatedAt)

fun FolderContentResponseDto.toDomain() = FolderContent(
    content.map { it.toDomain() },
    totalElements,
    currentPage,
    totalPages,
    sortBy,
    sortDirection
)

fun FolderContentDto.toDomain() = FolderContentItem(type, id, name, updatedAt, totalElements)

fun CreateFolderResponseDto.toDomain() = Folder(id, parentId, name)

fun FolderContentItemDto.toDomain() = FolderContentItem(type, id, name, updatedAt, totalElements)

// Domain to DTO
fun Document.toDto() = DocumentDto(id, folderId, name, url, pageCount, updatedAt)

fun Folder.toCreateFolderRequestDto() = CreateFolderRequestDto(name)

fun MoveItemsRequest.toDto() = MoveFolderRequestDto(documentIds, folderIds, destinationFolderId)

fun FolderContentItem.toDto() = FolderContentItemDto(id, name, type, totalElements, updatedAt)