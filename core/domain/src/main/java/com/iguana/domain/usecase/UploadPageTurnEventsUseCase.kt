package com.iguana.domain.usecase

import android.util.Log
import com.iguana.domain.model.record.PageTurnEvent
import com.iguana.domain.repository.RecordRepository
import javax.inject.Inject

class UploadPageTurnEventsUseCase @Inject constructor(
    private val recordRepository: RecordRepository
) {
    suspend operator fun invoke(documentId: Long, recordingId: Long) {
        val events = recordRepository.loadPageTurnEvents(documentId)
        recordRepository.uploadPageTurnEvents(recordingId, documentId, events)
    }
}