package com.iguana.data.mapper

import com.iguana.data.remote.model.GetSTTResultResponseDto
import com.iguana.domain.model.record.SttResult
import com.iguana.domain.model.record.SttItem

fun GetSTTResultResponseDto.toDomain(documentId: Long): SttResult {
    return SttResult(
        documentId = documentId,
        pageNumber = pageNumber,
        sttContents = contents.map {
            SttItem(
                startTime = it.startTime,
                endTime = it.endTime,
                content = it.content
            )
        },
        receivedTime = System.currentTimeMillis()
    )
}