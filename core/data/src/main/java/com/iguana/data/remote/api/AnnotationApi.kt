package com.iguana.data.remote.api

import com.iguana.data.remote.model.AnnotationResponseDto
import com.iguana.data.remote.model.CreateAnnotationRequestDto
import com.iguana.data.remote.model.GetAnnotationsResponseDto
import com.iguana.data.remote.model.UpdateAnnotationRequestDto
import retrofit2.http.*

interface AnnotationApi {

    // 주석 생성
    @POST("/api/documents/{documentId}/annotations")
    suspend fun createAnnotation(
        @Path("documentId") documentId: Long,
        @Body request: CreateAnnotationRequestDto
    ): AnnotationResponseDto

    // 주석 조회
    @GET("/api/documents/{documentId}/annotations")
    suspend fun getAnnotations(
        @Path("documentId") documentId: Long,
        @Query("pageNumbers") pageNumbers: List<Int>
    ): GetAnnotationsResponseDto

    // 주석 수정
    @PUT("/api/documents/{documentId}/annotations/{annotationId}")
    suspend fun updateAnnotation(
        @Path("documentId") documentId: Long,
        @Path("annotationId") annotationId: Long,
        @Body request: UpdateAnnotationRequestDto
    ): AnnotationResponseDto

    // 주석 삭제
    @DELETE("/api/documents/{documentId}/annotations/{annotationId}")
    suspend fun deleteAnnotation(
        @Path("documentId") documentId: Long,
        @Path("annotationId") annotationId: Long
    )
}