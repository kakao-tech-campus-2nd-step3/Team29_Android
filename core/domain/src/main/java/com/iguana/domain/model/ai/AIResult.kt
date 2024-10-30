package com.iguana.domain.model.ai

import android.text.Html
import android.text.Spanned
import androidx.core.text.parseAsHtml

data class AIResult(
    val documentId: Long,
    val pageNumber: Int,
    val summary: String?,
    val problem: String?
) {
    // 요약이 있는지 확인하는 프로퍼티
    val hasSummary: Boolean
        get() = !summary.isNullOrEmpty()

    // 문제가 있는지 확인하는 프로퍼티
    val hasProblem: Boolean
        get() = !problem.isNullOrEmpty()

    // HTML 포맷을 처리한 요약 반환
    val formattedSummary: Spanned
        get() = (summary?.takeIf { it.isNotEmpty() } ?: "요약 이용 불가").parseAsHtml()

    // HTML 포맷을 처리한 문제 반환
    val formattedProblem: Spanned
        get() = (problem?.takeIf { it.isNotEmpty() } ?: "문제 이용 불가").parseAsHtml()

}

