package com.iguana.data.remote.model

data class PageTurnEventRequestDto(
    val recordingId: Long,
    val events: List<PageTurnEvent>
)

data class PageTurnEvent(
    val prevPage: Int,
    val nextPage: Int,
    val timestamp: Double
)

data class RecordingUploadRequestDto(
    val documentName: String,
    val audioData: String // Base64 encoded
)

data class RecordingUploadResponseDto(
    val recordingId: Long,
    val documentId: Long,
    val url: String,
    val createdAt: String // ISO 8601 format
)



