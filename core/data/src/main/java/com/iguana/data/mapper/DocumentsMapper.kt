package com.iguana.data.mapper

import com.iguana.data.remote.model.*
import com.iguana.domain.model.*
import java.text.SimpleDateFormat
import java.util.*

// GetFolderContentResponseDto -> FolderContentItem
fun GetFolderContentResponseDto.toDomain(): FolderContentItem {
    return FolderContentItem(
        type = folderAndDocumentResponseType,
        id = response?.id ?: -1L,
        name = response?.name ?: "",
        updatedAt = response?.updatedAt ?: SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
        totalElements = response?.totalElements ?: -1
    )
}

// CreateFolderResponseDto -> Folder
fun CreateFolderResponseDto.toDomain(): Folder {
    return Folder(
        id = id,
        parentId = parentId,
        name = name
    )
}

// GetDocumentsResponseDto -> Document
fun GetDocumentsResponseDto.toDomain(): Document {
    return Document(
        id = id,
        folderId = null,  // folderId는 dto에 없으므로 null로 설정
        name = name,
        url = url,
        pageCount = null, // pageCount가 dto에 없으므로 null로 설정
        updatedAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
    )
}

// UpdateDocumentNameResponseDto -> Document
fun UpdateDocumentNameResponseDto.toDomain(): Document {
    return Document(
        id = id,
        folderId = null,  // folderId는 dto에 없으므로 null로 설정
        name = name,
        url = url,
        pageCount = null, // pageCount가 dto에 없으므로 null로 설정
        updatedAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
    )
}

// FolderContentItemDto -> FolderContentItem
fun FolderContentItemDto.toDomain(): FolderContentItem {
    return FolderContentItem(
        type = type,
        id = id,
        name = name,
        updatedAt = updatedAt,
        totalElements = totalElements
    )
}

// MoveItemsRequest -> MoveFolderRequestDto
fun MoveItemsRequest.toDto(): MoveFolderRequestDto {
    return MoveFolderRequestDto(
        documentIds = documentIds,
        folderIds = folderIds,
        destinationFolderId = destinationFolderId
    )
}

// CreateDocumentResponseDto -> Document
fun CreateDocumentResponseDto.toDomain(): Document {
    return Document(
        id = id,
        folderId = null,  // folderId는 dto에 없으므로 null로 설정
        name = name,
        url = url,
        pageCount = null, // pageCount가 dto에 없으므로 null로 설정
        updatedAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
    )
}