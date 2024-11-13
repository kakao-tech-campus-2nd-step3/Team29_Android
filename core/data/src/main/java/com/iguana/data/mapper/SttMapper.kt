package com.iguana.data.mapper

import com.iguana.data.remote.model.GetSTTResultResponseDto
import com.iguana.domain.model.record.SttResult

fun GetSTTResultResponseDto.toDomain(documentId: Long): SttResult {
    return SttResult(
        documentId = documentId,
        pageNumber = pageNumber,
        sttContents = contents.map {
            it.content
        },
        sttStartTime = contents.map { it.startTime },
        sttEndTime = contents.map { it.endTime },
        receivedTime = System.currentTimeMillis()
    )
}