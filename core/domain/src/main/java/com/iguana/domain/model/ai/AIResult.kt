package com.iguana.domain.model.ai

import android.text.Html

data class AIResult(
    val documentId: Long,
    val pageNumber: Int,
    val summary: String?,
    val problem: String?
) {
    fun hasSummary(): Boolean {
        return !summary.isNullOrEmpty()
    }

    fun hasProblem(): Boolean {
        return !problem.isNullOrEmpty()
    }

    // HTML 포맷을 처리한 요약 반환
    fun getFormattedSummary(): CharSequence {
        val formattedSummary = summary?.takeIf { it.isNotEmpty() } ?: "요약 이용 불가"
        return Html.fromHtml(formattedSummary)
    }

    // HTML 포맷을 처리한 문제 반환
    fun getFormattedProblem(): CharSequence {
        val formattedProblem = problem?.takeIf { it.isNotEmpty() } ?: "문제 이용 불가"
        return Html.fromHtml(formattedProblem)
    }
}

