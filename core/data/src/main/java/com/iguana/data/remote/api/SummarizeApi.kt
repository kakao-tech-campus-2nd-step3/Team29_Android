package com.iguana.data.remote.api

import com.iguana.data.remote.model.StatusCheckByPageResponseDto
import com.iguana.data.remote.model.StatusCheckResponseDto
import com.iguana.data.remote.model.SummarizeRequestDto
import com.iguana.data.remote.model.SummarizeResponseDto
import com.iguana.data.remote.model.SummarizeResultsByPageResponseDto
import com.iguana.data.remote.model.SummarizeResultsResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SummarizeApi {
    // 요약 생성
    @POST("/api/ai/llm")
    suspend fun requestSummarization(
        @Body request: SummarizeRequestDto
    ): Response<SummarizeResponseDto>

    // 요약 상태 확인
    @GET("/api/ai/llm/status/{documentId}")
    suspend fun checkStatus(
        @Path("documentId") documentId: Long
    ): Response<StatusCheckResponseDto>

    // 요약 결과 조회
    @GET("/api/ai/llm/results/{documentId}")
    suspend fun getSummarization(
        @Path("documentId") documentId: Long
    ): Response<SummarizeResultsResponseDto>

    // 요약 상태 확인 (페이지별)
    @GET("/api/ai/llm/status/{documentId}/{pageNumber}")
    suspend fun checkStatusByPage(
        @Path("documentId") documentId: Long,
        @Path("pageNumber") pageNumber: Int
    ): Response<StatusCheckByPageResponseDto>

    // 요약 결과 조회 (페이지별)
    @GET("/api/ai/llm/results/{documentId}/{pageNumber}")
    suspend fun getSummarizationByPage(
        @Path("documentId") documentId: Long,
        @Path("pageNumber") pageNumber: Int
    ): Response<SummarizeResultsByPageResponseDto>
}