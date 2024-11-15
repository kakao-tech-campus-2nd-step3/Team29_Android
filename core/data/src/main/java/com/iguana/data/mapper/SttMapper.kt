package com.iguana.data.mapper

import com.iguana.data.remote.model.GetSTTResultResponseDto
import com.iguana.data.remote.model.GetSTTStatusResponseDto
import com.iguana.domain.model.record.SttResult
import com.iguana.domain.model.record.SttItem
import com.iguana.domain.model.record.SttStatus
import com.iguana.domain.model.record.SttStatusResultByPage

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

fun GetSTTStatusResponseDto.toDomain(documentId: Long): SttStatusResultByPage {
    return SttStatusResultByPage(
        status = when (status) {
            "NOT_REQUESTED" -> SttStatus.NOT_REQUESTED
            "PENDING" -> SttStatus.PENDING
            "IN_PROGRESS" -> SttStatus.IN_PROGRESS
            "COMPLETED" -> SttStatus.COMPLETED
            "FAILED" -> SttStatus.FAILED
            else -> throw IllegalArgumentException("Invalid status: $status")
        }
    )
}