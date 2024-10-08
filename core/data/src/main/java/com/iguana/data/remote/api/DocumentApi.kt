package com.iguana.data.remote.api

import com.iguana.data.remote.model.CreateFolderRequestDto
import com.iguana.data.remote.model.CreateFolderResponseDto
import com.iguana.data.remote.model.DocumentDto
import com.iguana.data.remote.model.FolderContentItemDto
import com.iguana.data.remote.model.FolderContentResponseDto
import com.iguana.data.remote.model.MoveFolderRequestDto
import com.iguana.domain.model.FolderContentItem
import okhttp3.MultipartBody
import retrofit2.http.*

interface DocumentApi {
    @Multipart
    @POST("/api/folders/{folderId}/document")
    suspend fun uploadDocument(
        @Path("folderId") folderId: Long,
        @Part file: MultipartBody.Part
    ): DocumentDto

    @GET("/api/folders/{folderId}")
    suspend fun getFolderContents(
        @Path("folderId") folderId: Long?,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sortBy") sortBy: String = "updatedAt",
        @Query("sortDirection") sortDirection: String = "DESC"
    ): FolderContentResponseDto

    @GET("/api/documents")
    suspend fun getDocuments(@Query("documentIds") documentIds: List<Long>): List<DocumentDto>

    @GET("/api/documents/{documentId}")
    suspend fun getDocumentDetails(@Path("documentId") documentId: Long): DocumentDto

    @PUT("/api/documents/{documentId}/name")
    suspend fun updateDocumentName(
        @Path("documentId") documentId: Long,
        @Body name: Map<String, String>
    ): DocumentDto

    @DELETE("/api/documents/{documentId}")
    suspend fun deleteDocument(@Path("documentId") documentId: Long)

    @POST("/api/folders/{parentFolderId}")
    suspend fun createFolder(
        @Path("parentFolderId") parentFolderId: Long,
        @Body request: CreateFolderRequestDto
    ): CreateFolderResponseDto

    @DELETE("/api/folders/{folderId}")
    suspend fun deleteFolder(@Path("folderId") folderId: Long)

    @POST("/api/folders/move")
    suspend fun moveItems(@Body request: MoveFolderRequestDto): MoveFolderRequestDto

    @PUT("/api/folders/{folderId}/name")
    suspend fun updateFolderName(
        @Path("folderId") folderId: Long,
        @Body name: Map<String, String>
    ): FolderContentItemDto
}