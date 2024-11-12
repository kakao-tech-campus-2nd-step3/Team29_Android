package com.iguana.domain.repository

import com.iguana.domain.model.*
import java.io.File
import kotlinx.coroutines.flow.Flow

interface DocumentsRepository {
    suspend fun getAllDocuments(): FolderContent
    suspend fun uploadDocument(folderId: Long, file: File, documentName: String): Document?
    suspend fun getFolderContents(folderId: Long): FolderContent
    suspend fun getDocuments(folderId: Long, documentIds: List<Long>): List<Document>
    suspend fun deleteDocument(folderId: Long, documentId: Long)
    suspend fun createFolder(parentFolderId: Long, name: String): Folder
    suspend fun deleteFolder(folderId: Long)
    suspend fun moveItems(request: MoveItemsRequest): Boolean
    suspend fun updateFolderName(folderId: Long, newName: String) : FolderContentItem?
    suspend fun updateDocumentName(folderId: Long, documentId: Long, newName: String): Document
}