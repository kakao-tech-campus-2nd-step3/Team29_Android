package com.iguana.data.remote.api

import com.iguana.data.remote.model.CreateDocumentResponseDto
import com.iguana.data.remote.model.CreateFolderRequestDto
import com.iguana.data.remote.model.CreateFolderResponseDto
import com.iguana.data.remote.model.FolderOrDocumentResponseDto
import com.iguana.data.remote.model.GetDocumentsResponseDto
import com.iguana.data.remote.model.GetFolderContentResponseDto
import com.iguana.data.remote.model.MoveFolderRequestDto
import com.iguana.data.remote.model.MoveFolderResponseDto
import com.iguana.data.remote.model.UpdateContentNameRequestDto
import com.iguana.data.remote.model.UpdateDocumentNameResponseDto
import com.iguana.data.remote.model.UpdateFolderNameResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface DocumentApi {
    @Multipart
    @POST("/api/folders/{folderId}/documents")
    suspend fun uploadDocument(
        @Path("folderId") folderId: Long,
        @Part pdfFile: MultipartBody.Part,
        @Part("documentSaveRequest") documentSaveRequest: RequestBody
    ): CreateDocumentResponseDto

    @GET("/api/folders/{id}")
    suspend fun getFolderContents(
        @Path("id") folderId: Long
    ): List<GetFolderContentResponseDto>

    @GET("/api/folders/{folderId}/documents")
    suspend fun getDocuments(
        @Path("folderId") folderId: Long,
        @Query("documentIds") documentIds: List<Long>
    ): List<GetDocumentsResponseDto>

    @DELETE("/api/folders/{folderId}/documents/{id}")
    suspend fun deleteDocument(
        @Path("folderId") folderId: Long,
        @Path("id") documentId: Long
    )

    @POST("/api/folders")
    suspend fun createFolder(
        @Body request: CreateFolderRequestDto
    ): CreateFolderResponseDto

    @DELETE("/api/folders/{folderId}")
    suspend fun deleteFolder(@Path("folderId") folderId: Long)

    @POST("/api/folders/move")
    suspend fun moveItems(@Body request: MoveFolderRequestDto): MoveFolderResponseDto

    @PUT("/api/folders/{id}")
    suspend fun updateFolderName(
        @Path("id") id: Long,
        @Body request: UpdateContentNameRequestDto
    ): UpdateFolderNameResponseDto

    @PUT("/api/folders/{folderId}/documents/{documentId}")
    suspend fun updateDocumentName(
        @Path("folderId") folderId: Long,
        @Path("documentId") documentId: Long,
        @Body request: UpdateContentNameRequestDto
    ): UpdateDocumentNameResponseDto
}