package com.iguana.domain.usecase

import android.util.Log
import com.iguana.domain.repository.AIRepository
import javax.inject.Inject

class RequestAISummaryUseCase @Inject constructor(
    private val aiRepository: AIRepository
) {
    suspend operator fun invoke(documentId: Long, pages: List<Int>) {
        Log.d("testt", "SummarizeRequestDto(documentId, pages): ${pages}")
        aiRepository.requestSummarization(documentId, pages)
    }
}