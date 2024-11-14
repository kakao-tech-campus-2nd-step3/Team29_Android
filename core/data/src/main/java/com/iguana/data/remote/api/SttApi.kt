package com.iguana.data.remote.api

import com.iguana.data.remote.model.GetSTTResultResponseDto
import retrofit2.http.POST
import retrofit2.http.Path

interface SttApi {
    @POST("/api/stt/documents/{documentId}/pages/{pageNumber}")
    suspend fun getSTTResult(
        @Path("documentId") documentId: Long,
        @Path("pageNumber") pageNumber: Int,
    ): GetSTTResultResponseDto
}