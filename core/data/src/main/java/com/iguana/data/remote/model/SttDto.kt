package com.iguana.data.remote.model

data class GetSTTResultResponseDto(
    val pageNumber: Int,
    val contents: List<STTContent>
)

data class STTContent(
    val content: String,
    val startTime: Int,
    val endTime: Int)