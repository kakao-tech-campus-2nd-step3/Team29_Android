package com.iguana.domain.usecase

import com.iguana.domain.model.FolderContent
import com.iguana.domain.model.ai.AIStatusResultByPage
import com.iguana.domain.model.ai.SummarizationStatus
import com.iguana.domain.repository.AIRepository
import javax.inject.Inject

// 요약 상태 확인 (페이지별) UseCase
class GetAIStatusByPageUseCase @Inject constructor(
    private val aiRepository: AIRepository
) {
    suspend operator fun invoke(documentId:Long, pageNumber:Int): AIStatusResultByPage {
        return aiRepository.checkStatusByPage(documentId, pageNumber)
    }
}