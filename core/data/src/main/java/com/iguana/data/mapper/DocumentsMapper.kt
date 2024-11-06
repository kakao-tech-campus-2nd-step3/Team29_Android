package com.iguana.data.mapper

import com.iguana.data.remote.model.*
import com.iguana.domain.model.*
import java.text.SimpleDateFormat
import java.util.*

// DTO to Domain
fun DocumentDto.toDomain() = Document(
    id = id,
    folderId = folderId,
    name = name,
    url = url,
    pageCount = pageCount,
    updatedAt = updatedAt ?: SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
)

fun FolderContentDto.toDomain() = FolderContentItem(
    type = when {
        type?.uppercase() == "FOLDER" -> "FOLDER"
        type?.uppercase() == "DOCUMENT" -> "DOCUMENT"  // PDF 파일은 DOCUMENT로 처리
        else -> "DOCUMENT"  // 기본값도 DOCUMENT로 설정
    },
    id = id,
    name = name,
    updatedAt = updatedAt ?: SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
    totalElements = totalElements
)

fun CreateFolderResponseDto.toDomain() = Folder(id, parentId, name)

fun FolderContentItemDto.toDomain() = FolderContentItem(type, id, name, updatedAt, totalElements)

// Domain to DTO
fun Document.toDto() = DocumentDto(id, folderId, name, url, pageCount, updatedAt)

fun Folder.toCreateFolderRequestDto(parentFolderId: Long) = CreateFolderRequestDto(name, parentFolderId)

fun MoveItemsRequest.toDto() = MoveFolderRequestDto(documentIds, folderIds, destinationFolderId)

fun FolderContentItem.toDto() = FolderContentItemDto(id, name, type, totalElements, updatedAt)