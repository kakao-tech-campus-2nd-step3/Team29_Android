package com.iguana.domain.model.record

data class SttResult(
    val documentId: Long,
    val pageNumber: Int,
    val sttContents: List<String>,
    val sttStartTime: List<Int>, // STT 시작 시간
    val sttEndTime: List<Int>, // STT 종료 시간
    val receivedTime: Long, // 받은 시간
)