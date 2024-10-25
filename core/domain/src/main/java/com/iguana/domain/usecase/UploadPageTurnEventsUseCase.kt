package com.iguana.domain.usecase

import com.iguana.domain.model.record.PageTurnEvent
import com.iguana.domain.repository.RecordRepository
import javax.inject.Inject

class UploadPageTurnEventsUseCase @Inject constructor(
    private val recordRepository: RecordRepository
) {
    suspend operator fun invoke(documentId: Long, recordingId: Long, events: List<PageTurnEvent>) {
        recordRepository.uploadPageTurnEvents(recordingId, events)
    }
}