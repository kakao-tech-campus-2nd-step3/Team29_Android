package com.iguana.domain.model.ai
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

    fun getFormattedSummary(): String {
        return summary?.takeIf { it.isNotEmpty() } ?: "요약 이용 불가"
    }

    fun getFormattedProblem(): String {
        return problem?.takeIf { it.isNotEmpty() } ?: "문제 이용 불가"
    }
}
