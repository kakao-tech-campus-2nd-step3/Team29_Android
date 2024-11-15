package com.iguana.domain.usecase

import com.iguana.domain.model.record.SttItem
import com.iguana.domain.model.record.SttResult
import com.iguana.domain.repository.SttRepository
import javax.inject.Inject

class GetSTTResultByPageUseCase @Inject constructor(
    private val sttRepository: SttRepository
) {
    suspend operator fun invoke(documentId: Long, pageNumber: Int): SttResult {
       return sttRepository.getSTTResult(documentId, pageNumber)
    }
}