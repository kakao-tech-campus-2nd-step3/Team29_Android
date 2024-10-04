package com.iguana.data.repository

import android.content.Context
import com.iguana.data.mapper.toDomain
import com.iguana.data.mapper.toDto
import com.iguana.data.remote.api.DocumentApi
import com.iguana.data.remote.model.CreateFolderRequestDto
import com.iguana.domain.model.Document
import com.iguana.domain.model.Folder
import com.iguana.domain.model.FolderContent
import com.iguana.domain.model.MoveItemsRequest
import com.iguana.domain.repository.DocumentsRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import retrofit2.HttpException

class DocumentsRepositoryImpl @Inject constructor(
    private val api: DocumentApi
) : DocumentsRepository {

    override suspend fun uploadDocument(folderId: Long, file: File): Result<Document> = try {
        val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        val response = api.uploadDocument(folderId, body)
        Result.success(response.toDomain())
    } catch (e: HttpException) {
        Result.failure(e)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getFolderContents(folderId: Long?, page: Int, size: Int, sortBy: String, sortDirection: String): Result<FolderContent> = try {
        val response = api.getFolderContents(folderId, page, size, sortBy, sortDirection)
        Result.success(response.toDomain())
    } catch (e: HttpException) {
        Result.failure(e)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getDocuments(documentIds: List<Long>): Result<List<Document>> = try {
        val response = api.getDocuments(documentIds)
        Result.success(response.map { it.toDomain() })
    } catch (e: HttpException) {
        Result.failure(e)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getDocumentDetails(documentId: Long): Result<Document> = try {
        val response = api.getDocumentDetails(documentId)
        Result.success(response.toDomain())
    } catch (e: HttpException) {
        Result.failure(e)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateDocumentName(documentId: Long, name: String): Result<Document> = try {
        val response = api.updateDocumentName(documentId, mapOf("name" to name))
        Result.success(response.toDomain())
    } catch (e: HttpException) {
        Result.failure(e)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteDocument(documentId: Long): Result<Unit> = try {
        api.deleteDocument(documentId)
        Result.success(Unit)
    } catch (e: HttpException) {
        Result.failure(e)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun createFolder(parentFolderId: Long, name: String): Result<Folder> = try {
        val request = CreateFolderRequestDto(name)
        val response = api.createFolder(parentFolderId, request)
        Result.success(response.toDomain())
    } catch (e: HttpException) {
        Result.failure(e)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteFolder(folderId: Long): Result<Unit> = try {
        api.deleteFolder(folderId)
        Result.success(Unit)
    } catch (e: HttpException) {
        Result.failure(e)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun moveItems(request: MoveItemsRequest): Result<Unit> = try {
        api.moveItems(request.toDto())
        Result.success(Unit)
    } catch (e: HttpException) {
        Result.failure(e)
    } catch (e: Exception) {
        Result.failure(e)
    }

    companion object {
        private const val TAG = "DocumentsRepositoryImpl"
    }
}