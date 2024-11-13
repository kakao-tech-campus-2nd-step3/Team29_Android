package com.iguana.domain.model.record

data class SttResult(
    val documentId: Long,
    val pageNumber: Int,
    val sttContents: List<String>,
    val receivedTime: Long // 수신 시간 추가
)