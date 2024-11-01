package com.iguana.domain.usecase

import com.iguana.domain.repository.RecordRepository
import javax.inject.Inject

class DeletePageTurnEventsUseCase @Inject constructor(
    private val recordRepository: RecordRepository
) {
    suspend operator fun invoke(documentId: Long) {
        recordRepository.deletePageTurnEvents(documentId)
    }
}