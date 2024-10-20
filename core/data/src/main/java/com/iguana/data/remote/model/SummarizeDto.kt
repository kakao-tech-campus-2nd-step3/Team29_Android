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

data class StatusCheckByPageResponseDto(
    val status: String // "IN_PROGRESS", "COMPLETED", etc.
)

data class SummarizeResultsResponseDto(
    val documentId: Long,
    val totalPages: Int,
    val results: List<PageResultDto>
)

data class PageResultDto(
    val pageNumber: Int,
    val content: PageContentDto
)

data class PageContentDto(
    val summary: String?, // 요약 내용 (HTML, Markdown 등으로 포맷 가능)
    val problem: String? // 문제 내용 (HTML, Markdown 등으로 포맷 가능)
)