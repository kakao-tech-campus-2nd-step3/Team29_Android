package com.iguana.data.mapper

import com.iguana.data.remote.model.*
import com.iguana.domain.model.ai.AIResult
import com.iguana.domain.model.ai.AIStatusResult
import com.iguana.domain.model.ai.AIStatusResultByPage
import com.iguana.domain.model.ai.SummarizationStatus

// 요약 결과 매퍼
fun SummarizeResultsResponseDto.toDomain(): List<AIResult> {
    return results.map { pageResult ->
        AIResult(
            documentId = this.documentId,
            pageNumber = pageResult.pageNumber,
            summary = pageResult.content.summary,
            problem = pageResult.content.problem
        )
    }
}

// 상태 체크 응답 매퍼
fun StatusCheckResponseDto.toDomain(): AIStatusResult {
    return AIStatusResult(
        documentId = this.documentId,
        overallStatus = when (this.overallStatus) {
            "IN_PROGRESS" -> SummarizationStatus.IN_PROGRESS
            "COMPLETED" -> SummarizationStatus.COMPLETED
            else -> SummarizationStatus.FAILED
        },
        totalPages = this.totalPages,
        completedPages = this.completedPages
    )
}

// 상태 체크 응답 (페이지별) 매퍼
fun StatusCheckByPageResponseDto.toDomain(): AIStatusResultByPage {
    return AIStatusResultByPage(
        status = when (this.status) {
            "IN_PROGRESS" -> SummarizationStatus.IN_PROGRESS
            "COMPLETED" -> SummarizationStatus.COMPLETED
            "NOT_REQUESTED" -> SummarizationStatus.NOT_REQUESTED
            else -> SummarizationStatus.FAILED
        }
    )
}
