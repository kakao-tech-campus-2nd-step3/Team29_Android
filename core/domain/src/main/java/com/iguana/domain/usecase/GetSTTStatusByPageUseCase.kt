package com.iguana.domain.usecase

import com.iguana.domain.model.record.SttStatus
import com.iguana.domain.model.record.SttStatusResultByPage
import com.iguana.domain.repository.SttRepository
import javax.inject.Inject

class GetSTTStatusByPageUseCase @Inject constructor(
    private val sttRepository: SttRepository
) {
    suspend operator fun invoke(documentId: Long, pageNumber: Int): SttStatusResultByPage {
        // TODO: 아래 데이터는 임시 데이터로 서버 구현 시 변경 필요
        return SttStatusResultByPage(SttStatus.COMPLETED)
    }
}