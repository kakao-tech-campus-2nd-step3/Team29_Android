package com.iguana.domain.usecase

import com.iguana.domain.model.ai.AIResult
import com.iguana.domain.repository.AIRepository
import javax.inject.Inject

class GetAIResultByPageUseCase @Inject constructor(
    private val aiRepository: AIRepository
) {
    suspend operator fun invoke(documentId:Long, pageNumber:Int): AIResult {
        return aiRepository.getSummarizationByPage(documentId, pageNumber)
    }
}