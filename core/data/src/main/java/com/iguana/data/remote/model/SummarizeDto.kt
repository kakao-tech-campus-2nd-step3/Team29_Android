package com.iguana.data.remote.model

data class SummarizeRequestDto(
    val documentId: Long,
    var pages: List<Int>
)

data class SummarizeResponseDto(
    val documentId: Long,
    val createdAt: String
)

data class StatusCheckResponseDto(
    val documentId: Long,
    val overallStatus: String, // "IN_PROGRESS", "COMPLETED", etc.
    val totalPages: Int,
    val completedPages: Int
)
