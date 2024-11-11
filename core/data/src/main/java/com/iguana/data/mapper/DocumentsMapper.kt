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

fun FolderContentItemDto.toDomain(): FolderContentItem {
    return FolderContentItem(
        type = type,            // String
        id = id,               // Long
        name = name,           // String
        updatedAt = updatedAt, // String
        totalElements = totalElements  // Int
    )
}

fun FolderContentResponseDto.toDomain(): FolderContentItem {
    return FolderContentItem(
        type = folderAndDocumentResponseType,
        id = response?.id ?: -1L,
        name = response?.name ?: "",
        updatedAt = response?.updatedAt ?: SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
        totalElements = response?.totalElements ?: -1
    )
}

// Domain to DTO
fun Document.toDto() = DocumentDto(id, folderId, name, url, pageCount, updatedAt)

fun Folder.toCreateFolderRequestDto(parentFolderId: Long) = CreateFolderRequestDto(name, parentFolderId)

fun MoveItemsRequest.toDto() = MoveFolderRequestDto(documentIds, folderIds, destinationFolderId)

fun FolderContentItem.toDto(): FolderContentItemDto {
    return FolderContentItemDto(
        type = type,            // String
        id = id,               // Long
        name = name,           // String
        updatedAt = updatedAt, // String
        totalElements = totalElements  // Int
    )
}