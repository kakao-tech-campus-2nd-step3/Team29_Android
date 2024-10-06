package com.iguana.domain.repository

import com.iguana.domain.model.*
import java.io.File
import kotlinx.coroutines.flow.Flow

interface DocumentsRepository {
    fun getAllDocuments(): Flow<FolderContent>
    fun getSubItems(folderId: Long): Flow<FolderContent>
    suspend fun uploadDocument(folderId: Long, file: File): Result<Document>
    suspend fun getFolderContents(folderId: Long?, page: Int, size: Int, sortBy: String, sortDirection: String): Result<FolderContent>
    suspend fun getDocuments(documentIds: List<Long>): Result<List<Document>>
    suspend fun getDocumentDetails(documentId: Long): Result<Document>
    suspend fun updateDocumentName(documentId: Long, name: String): Result<Document>
    suspend fun deleteDocument(documentId: Long): Result<Unit>
    suspend fun createFolder(parentFolderId: Long, name: String): Result<Folder>
    suspend fun deleteFolder(folderId: Long): Result<Unit>
    suspend fun moveItems(request: MoveItemsRequest): Result<Unit>
}