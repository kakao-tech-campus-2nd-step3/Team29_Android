package com.iguana.data.repository

import com.iguana.data.utils.Logger
import com.iguana.data.mapper.toDomain
import com.iguana.data.mapper.toDto
import com.iguana.data.remote.api.DocumentApi
import com.iguana.data.remote.model.CreateFolderRequestDto
import com.iguana.data.remote.model.UpdateContentNameRequestDto
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
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.*

class DocumentsRepositoryImpl @Inject constructor(
    private val api: DocumentApi
) : DocumentsRepository {
    override suspend fun getAllDocuments(): FolderContent = try {
        val response = api.getFolderContents(-1)
        Logger.d(TAG, "루트 폴더 응답 - 아이템 개수: ${response.size}")
        response.forEach { item ->
            Logger.d(
                TAG,
                "아이템 타입: ${item.folderAndDocumentResponseType}, 이름: ${item.response?.name}, 아이디: ${item.response?.id}"
            )
        }
        response.map { it.toDomain() }
    } catch (e: Exception) {
        Logger.e(TAG, "모든 문서 가져오기 중 예외 발생: ${e.message}", e)
        emptyList()
    }

    override suspend fun uploadDocument(
        folderId: Long,
        file: File,
        documentName: String
    ): Document? = try {
        // 1. 파일을 RequestBody로 변환
        val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
        val fileBody = MultipartBody.Part.createFormData("pdfFile", file.name, requestFile)

        // 2. JSON 데이터를 RequestBody로 변환
        val documentSaveRequestJson = "{\"name\":\"$documentName\"}"
        val requestBodyJson =
            documentSaveRequestJson.toRequestBody("application/json".toMediaTypeOrNull())

        // 3. Retrofit API 호출
        val response = api.uploadDocument(folderId, fileBody, requestBodyJson)

        // 4. 성공 시 도메인 모델로 변환
        response.toDomain()
    } catch (e: Exception) {
        Logger.e(TAG, "문서 업로드 중 예외 발생: ${e.message}", e)
        null
    }

    override suspend fun getFolderContents(folderId: Long): FolderContent = try {
        val response = api.getFolderContents(folderId)
        Logger.d(TAG, "폴더($folderId) 응답 - 아이템 개수: ${response.size}")
        response.forEach { item ->
            Logger.d(
                TAG,
                "아이템 타입: ${item.folderAndDocumentResponseType}, 이름: ${item.response?.name}"
            )
        }
        response.map { it.toDomain() }
    } catch (e: Exception) {
        Logger.e(TAG, "폴더 내용 가져오기 중 예외 발생: ${e.message}", e)
        emptyList()
    }

    override suspend fun getDocuments(folderId: Long, documentIds: List<Long>): List<Document> =
        try {
            val response = api.getDocuments(folderId, documentIds)
            response.map { it.toDomain() }
        } catch (e: Exception) {
            Logger.e(TAG, "문서 목록 가져오기 중 예외 발생: ${e.message}", e)
            emptyList()
        }

    override suspend fun deleteDocument(folderId: Long, documentId: Long) = try {
        api.deleteDocument(folderId, documentId)
    } catch (e: Exception) {
        Logger.e(TAG, "문서 삭제 중 예외 발생: ${e.message}", e)
    }

    override suspend fun createFolder(parentFolderId: Long, name: String): Folder = try {
        Logger.d(TAG, "Creating folder with name: $name, parentFolderId: $parentFolderId")
        val request = CreateFolderRequestDto(
            name = name,
            parentFolderId = if (parentFolderId == -1L) null else parentFolderId
        )
        api.createFolder(request).toDomain()
    } catch (e: Exception) {
        Logger.e(TAG, "폴더 생성 중 예외 발생: ${e.message}", e)
        Folder(id = -1, parentId = -1, name = "")
    }

    override suspend fun deleteFolder(folderId: Long) = try {
        val response = api.deleteFolder(folderId)
    } catch (e: Exception) {
        Logger.e(TAG, "폴더 삭제 중 예외 발생: ${e.message}", e)
    }

    override suspend fun moveItems(request: MoveItemsRequest): Boolean = try {
        api.moveItems(request.toDto())
        true
    } catch (e: Exception) {
        Logger.e(TAG, "항목 이동 중 예외 발생: ${e.message}", e)
        false
    }

    override suspend fun updateFolderName(folderId: Long, newName: String): FolderContentItem? = try {
        val request = UpdateContentNameRequestDto(name = newName)
        val response = api.updateFolderName(folderId, request)
        FolderContentItem(
            type = "FOLDER",
            id = response.id,
            name = response.name,
            updatedAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
            totalElements = 0
        )
    } catch (e: Exception) {
        Logger.e(TAG, "폴더 이름 업데이트 중 예외 발생: ${e.message}", e)
        null
    }

    override suspend fun updateDocumentName(folderId: Long, documentId: Long, newName: String): Document = try {
        val request = UpdateContentNameRequestDto(name = newName)
        val response = api.updateDocumentName(folderId, documentId, request)
        response.toDomain()
    } catch (e: Exception) {
        Logger.e(TAG, "문서 제목 업데이트 중 예외 발생: ${e.message}", e)
        Document(
            id = documentId,
            folderId = folderId,
            name = newName,
            url = null,
            pageCount = null,
            updatedAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        )
    }

    companion object {
        private const val TAG = "DocumentsRepositoryImpl"
    }
}
