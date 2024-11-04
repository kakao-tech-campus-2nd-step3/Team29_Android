package com.iguana.data.mapper

import com.iguana.data.remote.model.*
import com.iguana.domain.model.*
import java.text.SimpleDateFormat
import java.util.*

// DTO to Domain
fun DocumentDto.toDomain() = Document(id, folderId, name, url, pageCount, updatedAt)

fun FolderContentDto.toDomain() = FolderContentItem(
    type = when {
        type == null -> "FOLDER"  // type이 null이면 FOLDER로 설정
        type.uppercase() in listOf("FILE", "PDF", "DOCUMENT") -> "FILE"  // 파일 타입들은 FILE로 통일
        else -> type.uppercase()  // 그 외의 경우는 대문자로 변환
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