package com.iguana.domain.usecase
import com.iguana.domain.model.record.PageTurnEvent
import com.iguana.domain.repository.RecordRepository
import javax.inject.Inject

class SavePageTurnEventUseCase @Inject constructor(
    private val recordRepository: RecordRepository
) {
    suspend operator fun invoke(documentId: Long, prevPage: Int?, currentPage: Int, startTimeMillis: Long) {
            // 현재 시간과 녹음 시작 시간을 바탕으로 초 단위 타임스탬프 계산
            val timestamp = (System.currentTimeMillis() - startTimeMillis) / 1000.0

            // PageTurnEvent 생성
            val event = PageTurnEvent(
                documentId = documentId,
                prevPage = prevPage ?: currentPage,
                nextPage = currentPage,
                timestamp = timestamp
            )

            // Repository를 통해 이벤트 저장
            recordRepository.savePageTurnEvents(documentId, event)
        }
}
