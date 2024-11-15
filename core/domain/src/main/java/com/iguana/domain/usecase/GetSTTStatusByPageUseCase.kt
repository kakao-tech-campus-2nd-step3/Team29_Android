package com.iguana.domain.usecase

import com.iguana.domain.model.record.SttStatus
import com.iguana.domain.model.record.SttStatusResultByPage
import com.iguana.domain.repository.SttRepository
import javax.inject.Inject

class GetSTTStatusByPageUseCase @Inject constructor(
    private val sttRepository: SttRepository
) {
    suspend operator fun invoke(documentId: Long, pageNumber: Int): SttStatusResultByPage {
        return sttRepository.getSTTStatus(documentId, pageNumber)
    }
}