package com.iguana.domain.model.ai

enum class SummarizationStatus {
    NOT_REQUESTED,
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED
}

data class AIStatusResult(
    val documentId: Long,
    val overallStatus: SummarizationStatus,
    val totalPages: Int,
    val completedPages: Int
)

data class AIStatusResultByPage(
    val status: SummarizationStatus,
) {
    // 요약 작업이 완료되었는지 여부 확인
    fun isCompleted(): Boolean {
        return status == SummarizationStatus.COMPLETED
    }

    // 작업이 실패했는지 여부 확인
    fun isFailed(): Boolean {
        return status == SummarizationStatus.FAILED
    }

    // 요약 요청을 안했는지 여부 확인
    fun isNotRequested(): Boolean {
        return status == SummarizationStatus.NOT_REQUESTED
    }
    // 요약 진행 상태 중인지 확인
    fun isInProgress(): Boolean {
        return status == SummarizationStatus.IN_PROGRESS || status == SummarizationStatus.PENDING
    }
}