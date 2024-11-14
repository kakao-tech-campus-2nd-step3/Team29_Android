package com.iguana.data.mapper

import android.util.Log
import com.iguana.data.remote.model.*
import com.iguana.domain.model.*
import java.text.SimpleDateFormat
import java.util.*


// GetFolderContentResponseDto -> FolderContentItem
fun GetFolderContentResponseDto.toDomain(): FolderContentItem? {
    val response = this.response
    return when (folderAndDocumentResponseType) {
        "FOLDER" -> {
            val folder = response as? FolderResponseDto ?: return null  // response가 null이면 null 반환
            Log.d("testt", "Folder response: $response")
            FolderContentItem(
                type = folderAndDocumentResponseType,
                id = folder.id,
                name = folder.name,
                updatedAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
                totalElements = -1,
                url = null
            )
        }
        "DOCUMENT" -> {
            val document = response as? DocumentResponseDto ?: return null  // response가 null이면 null 반환
            Log.d("testt", "Document response: $response")
            FolderContentItem(
                type = folderAndDocumentResponseType,
                id = document.id,
                name = document.name,
                updatedAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
                totalElements = -1,
                url = document.url
            )
        }
        else -> null
    }
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