package com.iguana.data.remote.api

import com.iguana.data.remote.model.PageTurnEventRequestDto
import com.iguana.data.remote.model.RecordingUploadRequestDto
import com.iguana.data.remote.model.RecordingUploadResponseDto
import retrofit2.Response
import retrofit2.http.*
interface RecordApi {
    // 페이지 넘김 이벤트 기록
    @POST("/api/documents/{documentId}/recordings/page-turns")
    suspend fun recordPageTurnEvent(
        @Path("documentId") documentId: Long,
        @Body request: PageTurnEventRequestDto
    ): Response<Unit>

    // 녹음 파일 업로드
    @POST("/api/documents/{documentId}/recordings")
    suspend fun uploadRecording(
        @Path("documentId") documentId: Long,
        @Body request: RecordingUploadRequestDto
    ): Response<RecordingUploadResponseDto>
}