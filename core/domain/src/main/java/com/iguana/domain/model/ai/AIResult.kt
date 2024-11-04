package com.iguana.domain.model.ai

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
}

