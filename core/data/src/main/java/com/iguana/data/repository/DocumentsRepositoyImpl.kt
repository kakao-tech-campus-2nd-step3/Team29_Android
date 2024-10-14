package com.iguana.data.repository

import android.content.Context
import android.util.Log
import com.iguana.data.mapper.toDomain
import com.iguana.data.mapper.toDto
import com.iguana.data.remote.api.DocumentApi
import com.iguana.data.remote.model.CreateFolderRequestDto
import com.iguana.domain.model.Document
import com.iguana.domain.model.Folder
import com.iguana.domain.model.FolderContent
import com.iguana.domain.model.FolderContentItem
import com.iguana.domain.model.MoveItemsRequest
import com.iguana.domain.repository.DocumentsRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import retrofit2.HttpException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DocumentsRepositoryImpl @Inject constructor(
    private val api: DocumentApi
) : DocumentsRepository {
    override fun getAllDocuments(): Flow<FolderContent> = flow {
        try {
            val response = api.getFolderContents(
                folderId = null,
                sortBy = "updatedAt",
                sortDirection = "DESC"
            )
            val folderContent = response.toDomain()
            emit(folderContent)
        } catch (e: Exception) {
            Log.e(TAG, "모든 문서 가져오기 중 예외 발생: ${e.message}", e)
            emit(FolderContent(emptyList(), 0, 0, 0, "", ""))
        }
    }

    override fun getSubItems(folderId: Long): Flow<FolderContent> = flow {
        try {
            val response = api.getFolderContents(
                folderId = folderId,
                sortBy = "updatedAt",
                sortDirection = "DESC"
            )
            val folderContent = response.toDomain()
            emit(folderContent)
        } catch (e: Exception) {
            Log.e(TAG, "하위 항목 가져오기 중 예외 발생: ${e.message}", e)
            emit(FolderContent(emptyList(), 0, 0, 0, "", ""))
        }
    }

    override suspend fun uploadDocument(folderId: Long, file: File): Result<Document> = try {
        val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        val response = api.uploadDocument(folderId, body)
        Result.success(response.toDomain())
    } catch (e: Exception) {
        Log.e(TAG, "문서 업로드 중 예외 발생: ${e.message}", e)
        Result.failure(e)
    }

    override suspend fun getFolderContents(folderId: Long?, page: Int, size: Int, sortBy: String, sortDirection: String): Result<FolderContent> = try {
        val response = api.getFolderContents(folderId, page, size, sortBy, sortDirection)
        Result.success(response.toDomain())
    } catch (e: Exception) {
        Log.e(TAG, "폴더 내용 가져오기 중 예외 발생: ${e.message}", e)
        Result.failure(e)
    }

    override suspend fun getDocuments(documentIds: List<Long>): Result<List<Document>> = try {
        val response = api.getDocuments(documentIds)
        Result.success(response.map { it.toDomain() })
    } catch (e: Exception) {
        Log.e(TAG, "문서 목록 가져오기 중 예외 발생: ${e.message}", e)
        Result.failure(e)
    }

    override suspend fun getDocumentDetails(documentId: Long): Result<Document> = try {
        val response = api.getDocumentDetails(documentId)
        Result.success(response.toDomain())
    } catch (e: Exception) {
        Log.e(TAG, "문서 상세 정보 가져오기 중 예외 발생: ${e.message}", e)
        Result.failure(e)
    }

    override suspend fun updateDocumentName(documentId: Long, name: String): Result<Document> = try {
        val response = api.updateDocumentName(documentId, mapOf("name" to name))
        Result.success(response.toDomain())
    } catch (e: Exception) {
        Log.e(TAG, "문서 이름 업데이트 중 예외 발생: ${e.message}", e)
        Result.failure(e)
    }

    override suspend fun deleteDocument(documentId: Long): Result<Unit> = try {
        api.deleteDocument(documentId)
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "문서 삭제 중 예외 발생: ${e.message}", e)
        Result.failure(e)
    }

    override suspend fun createFolder(parentFolderId: Long, name: String): Result<Folder> = try {
        val request = CreateFolderRequestDto(name)
        val response = api.createFolder(parentFolderId, request)
        Result.success(response.toDomain())
    } catch (e: Exception) {
        Log.e(TAG, "폴더 생성 중 예외 발생: ${e.message}", e)
        Result.failure(e)
    }

    override suspend fun deleteFolder(folderId: Long): Result<Unit> = try {
        api.deleteFolder(folderId)
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "폴더 삭제 중 예외 발생: ${e.message}", e)
        Result.failure(e)
    }

    override suspend fun moveItems(request: MoveItemsRequest): Result<Unit> = try {
        api.moveItems(request.toDto())
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "항목 이동 중 예외 발생: ${e.message}", e)
        Result.failure(e)
    }

    override suspend fun updateFolderName(folderId: Long, newName: String): Result<FolderContentItem> = try {
        val response = api.updateFolderName(folderId, mapOf("name" to newName))
        Result.success(response.toDomain())
    } catch (e: Exception) {
        Log.e(TAG, "폴더 이름 업데이트 중 예외 발생: ${e.message}", e)
        Result.failure(e)
    }

    companion object {
        private const val TAG = "DocumentsRepositoryImpl"
    }
}