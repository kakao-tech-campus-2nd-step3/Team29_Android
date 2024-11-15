package com.iguana.domain.model.record

enum class SttStatus {
    NOT_REQUESTED,
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED
}

data class SttStatusResultByPage(
    val status: SttStatus,
) {
    // 요약 작업이 완료되었는지 여부 확인
    fun isCompleted(): Boolean {
        return status == SttStatus.COMPLETED
    }

    // 작업이 실패했는지 여부 확인
    fun isFailed(): Boolean {
        return status == SttStatus.FAILED
    }

    // 요약 요청을 안했는지 여부 확인
    fun isNotRequested(): Boolean {
        return status == SttStatus.NOT_REQUESTED
    }
    // 요약 진행 상태 중인지 확인
    fun isInProgress(): Boolean {
        return status == SttStatus.IN_PROGRESS || status == SttStatus.PENDING
    }
}

data class SttStatusResult(
    val documentId: Long,
    val overallStatus: SttStatus,
    val totalPages: Int,
    val completedPages: Int
)