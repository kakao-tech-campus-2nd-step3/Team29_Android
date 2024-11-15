package com.iguana.domain.model.record

data class SttResult(
    val documentId: Long,
    val pageNumber: Int,
    val sttContents: List<SttItem>,
    val receivedTime: Long, // 받은 시간
)

data class SttItem(
    val startTime: Int,
    val endTime: Int,
    val content: String
)