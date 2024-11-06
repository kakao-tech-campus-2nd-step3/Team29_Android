package com.iguana.data.repository

import com.iguana.data.utils.Logger
import com.iguana.data.mapper.toDomain
import com.iguana.data.mapper.toDto
import com.iguana.data.remote.api.DocumentApi
import com.iguana.data.remote.model.CreateFolderRequestDto
import com.iguana.data.remote.model.UpdateFolderNameRequestDto
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class DocumentsRepositoryImpl @Inject constructor(
    private val api: DocumentApi
) : DocumentsRepository {
    override suspend fun getAllDocuments(): Result<FolderContent> = try {
        val folderContents = api.getRootFolderContents(
            page = 0,
            size = 20,
            sortBy = "updatedAt",
            sortDirection = "DESC"
        )
        
        // totalElements 값으로 type 구분
        val result = folderContents.map { content -> 
            FolderContentItem(
                type = when {
                    content.totalElements >= 0 -> "FOLDER"  // 0 이상이면 폴더
                    else -> "DOCUMENT"  // -1이면 문서
                },
                id = content.id,
                name = content.name,
                updatedAt = content.updatedAt ?: SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
                totalElements = content.totalElements
            )
        }
        
        Logger.d(TAG, "변환 결과:")
        result.forEach { item ->
            Logger.d(TAG, "- ${item.name} (type: ${item.type}, totalElements: ${item.totalElements})")
        }
        
        Result.success(result)
    } catch (e: Exception) {
        Logger.e(TAG, "모든 문서 가져오기 중 예외 발생: ${e.message}", e)
        Result.failure(e)
    }

    override suspend fun uploadDocument(folderId: Long, file: File, documentName: String): Result<Document> = try {
        // 1. 파일을 RequestBody로 변환
        val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
        val fileBody = MultipartBody.Part.createFormData("pdfFile", file.name, requestFile)

        // 2. JSON 데이터를 RequestBody로 변환
        val documentSaveRequestJson = http://chatgot.co.kr/ad/cpc_open.php?app=205&domain=perplexity.ai&type=1&aid=8291&browser=msedge&guid=202206125MnR%2Fy
        // "{\"name\":\"$documentName\"}"
        val requestBodyJson = documentSaveRequestJson.toRequestBody("application/json".toMediaTypeOrNull())

        // 3. Retrofit API 호출
        val response = api.uploadDocument(folderId, fileBody, requestBodyJson)
        
        // 4. DocumentDto를 Document로 변환
        Result.success(response.toDomain())
    } catch (e: Exception) {
        Logger.e(TAG, "문서 업로드 중 예외 발생: ${e.message}", e)
        Result.failure(e)
    }

    override suspend fun getFolderContents(
        folderId: Long,
        page: Int,
        size: Int,
        sortBy: String,
        sortDirection: String
    ): Result<FolderContent> = try {
        val response = api.getFolderContents(
            parentFolderId = folderId,
            page = page,
            size = size,
            sortBy = sortBy,
            sortDirection = sortDirection
        )
        Result.success(response.map { it.toDomain() })
    } catch (e: Exception) {
        Logger.e(TAG, "폴더 내용 가져오기 중 예외 발생: ${e.message}", e)
        Result.failure(e)
    }

    override suspend fun getDocuments(folderId: Long, documentIds: List<Long>): Result<List<Document>> = try {
        val response = api.getDocuments(folderId, documentIds)
        Result.success(response.body()?.map { it.toDomain() } ?: emptyList())
    } catch (e: Exception) {
        Logger.e(TAG, "문서 목록 가져오기 중 예외 발생: ${e.message}", e)
        Result.failure(e)
    }

    override suspend fun deleteDocument(documentId: Long): Result<Unit> = try {
        api.deleteDocument(documentId)
        Result.success(Unit)
    } catch (e: Exception) {
        Logger.e(TAG, "문서 삭제 중 예외 발생: ${e.message}", e)
        Result.failure(e)
    }

    override suspend fun createFolder(parentFolderId: Long, name: String): Result<Folder> = try {
        Logger.d(TAG, "Creating folder with name: $name, parentFolderId: $parentFolderId")
        val request = CreateFolderRequestDto(
            name = name,
            parentFolderId = if (parentFolderId == -1L) null else parentFolderId
        )
        val response = api.createFolder(request)
        Result.success(response.toDomain())
    } catch (e: Exception) {
        Logger.e(TAG, "폴더 생성 중 예외 발생: ${e.message}", e)
        Result.failure(e)
    }

    override suspend fun deleteFolder(folderId: Long): Result<Unit> = try {
        val response = api.deleteFolder(folderId)
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(HttpException(response))
        }
    } catch (e: Exception) {
        Logger.e(TAG, "폴더 삭제 중 예외 발생: ${e.message}", e)
        Result.failure(e)
    }

    override suspend fun moveItems(request: MoveItemsRequest): Result<Unit> = try {
        api.moveItems(request.toDto())
        Result.success(Unit)
    } catch (e: Exception) {
        Logger.e(TAG, "항목 이동 중 예외 발생: ${e.message}", e)
        Result.failure(e)
    }

    override suspend fun updateFolderName(folderId: Long, newName: String): Result<FolderContentItem> = try {
        val request = mapOf("name" to newName)
        val response = api.updateFolderName(folderId, request)
        val folderContentItem = FolderContentItem(
            type = "FOLDER",
            id = response.id,
            name = response.name,
            updatedAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
            totalElements = 0
        )
        Result.success(folderContentItem)
    } catch (e: Exception) {
        Logger.e(TAG, "폴더 이름 업데이트 중 예외 발생: ${e.message}", e)
        Result.failure(e)
    }

    companion object {
        private const val TAG = "DocumentsRepositoryImpl"
    }
}
