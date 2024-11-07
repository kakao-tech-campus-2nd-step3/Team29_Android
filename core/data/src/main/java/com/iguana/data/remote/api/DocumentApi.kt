package com.iguana.data.remote.api

import com.iguana.data.remote.model.CreateFolderRequestDto
import com.iguana.data.remote.model.CreateFolderResponseDto
import com.iguana.data.remote.model.DocumentDto
import com.iguana.data.remote.model.FolderContentDto
import com.iguana.data.remote.model.FolderContentItemDto
import com.iguana.data.remote.model.MoveFolderRequestDto
import com.iguana.data.remote.model.UpdateFolderNameRequestDto
import com.iguana.domain.model.FolderContentItem
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface DocumentApi {
    @Multipart
    @POST("/api/folders/{folderId}/documents")
    suspend fun uploadDocument(
        @Path("folderId") folderId: Long,
        @Part pdfFile: MultipartBody.Part,
        @Part("documentSaveRequest") documentSaveRequest: RequestBody
    ): DocumentDto

    @GET("/api/folders")
    suspend fun getRootFolderContents(
        @Query("parentFolderId") parentFolderId: Long? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sortBy") sortBy: String = "updatedAt",
        @Query("sortDirection") sortDirection: String = "DESC"
    ): List<FolderContentDto>

    @GET("/api/folders")
    suspend fun getFolderContents(
        @Query("parentFolderId") parentFolderId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sortBy") sortBy: String = "updatedAt",
        @Query("sortDirection") sortDirection: String = "DESC"
    ): List<FolderContentDto>

    @GET("/api/folders/{folderId}/documents")
    suspend fun getDocuments(
        @Path("folderId") folderId: Long,
        @Query("documentIds") documentIds: List<Long>
    ): Response<List<DocumentDto>>

    @DELETE("/api/folders/{folderId}/documents/{id}")
    suspend fun deleteDocument(
        @Path("folderId") folderId: Long,
        @Path("id") documentId: Long
    ): Response<Unit>

    @POST("/api/folders")
    suspend fun createFolder(
        @Body request: CreateFolderRequestDto
    ): CreateFolderResponseDto

    @DELETE("/api/folders/{folderId}")
    suspend fun deleteFolder(@Path("folderId") folderId: Long): Response<Unit>

    @POST("/api/folders/move")
    suspend fun moveItems(@Body request: MoveFolderRequestDto): MoveFolderRequestDto

    @PUT("/api/folders/{id}")
    suspend fun updateFolderName(
        @Path("id") id: Long,
        @Body request: Map<String, String>
    ): CreateFolderResponseDto

    @PUT("/api/folders/{folderId}/documents/{documentId}")
    suspend fun updateDocumentName(
        @Path("folderId") folderId: Long,
        @Path("documentId") documentId: Long,
        @Body request: Map<String, String>
    ): DocumentDto
}