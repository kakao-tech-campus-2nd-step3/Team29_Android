package com.iguana.domain.model.ai

enum class SummarizationStatus {
    IN_PROGRESS,
    COMPLETED,
    FAILED
}

data class AIStatusResult(
    val documentId: Long,
    val overallStatus: SummarizationStatus,
    val totalPages: Int,
    val completedPages: Int
) {
    // 진행률을 계산 (0 ~ 100%)
    fun getProgressPercentage(): Int {
        if (totalPages == 0) return 0
        return (completedPages * 100) / totalPages
    }

    // 요약 작업이 완료되었는지 여부 확인
    fun isCompleted(): Boolean {
        return overallStatus == SummarizationStatus.COMPLETED
    }

    // 작업이 실패했는지 여부 확인
    fun isFailed(): Boolean {
        return overallStatus == SummarizationStatus.FAILED
    }

    // 요약 진행 상태를 문자열로 반환
    fun getStatusMessage(): String {
        return when (overallStatus) {
            SummarizationStatus.IN_PROGRESS -> "요약이 진행 중입니다: ${getProgressPercentage()}% 완료"
            SummarizationStatus.COMPLETED -> "요약이 완료되었습니다."
            SummarizationStatus.FAILED -> "요약이 실패했습니다."
        }
    }
}
