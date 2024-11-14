package com.iguana.data.remote.api

import com.iguana.data.remote.model.GetSTTResultResponseDto
import com.iguana.data.remote.model.GetSTTStatusResponseDto
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SttApi {
    @GET("/api/stt/documents/{documentId}/pages/{pageNumber}")
    suspend fun getSTTResult(
        @Path("documentId") documentId: Long,
        @Path("pageNumber") pageNumber: Int,
    ): GetSTTResultResponseDto

    @GET("/api/ai/stt/status/{documentId}/{pageNumber}")
    suspend fun getSTTStatus(
        @Path("documentId") documentId: Long,
        @Path("pageNumber") pageNumber: Int,
    ): GetSTTStatusResponseDto
}