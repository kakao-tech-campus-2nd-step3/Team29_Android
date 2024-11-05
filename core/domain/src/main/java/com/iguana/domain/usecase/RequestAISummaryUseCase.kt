package com.iguana.domain.usecase

import com.iguana.domain.repository.AIRepository
import javax.inject.Inject

class RequestAISummaryUseCase @Inject constructor(
    private val aiRepository: AIRepository
) {
    suspend operator fun invoke(documentId: Long, pages: List<Int>) {
        aiRepository.requestSummarization(documentId, pages)
    }
}