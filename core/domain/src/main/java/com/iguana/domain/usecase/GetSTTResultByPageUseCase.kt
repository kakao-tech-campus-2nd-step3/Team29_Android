package com.iguana.domain.usecase

import com.iguana.domain.model.record.SttResult
import com.iguana.domain.repository.SttRepository
import javax.inject.Inject

class GetSTTResultByPageUseCase @Inject constructor(
    private val sttRepository: SttRepository
) {
    suspend operator fun invoke(documentId: Long, pageNumber: Int): SttResult {
        // TODO: 아래 데이터는 임시 데이터로 서버 구현 시 주석 해제
//        return sttRepository.getSTTResult(documentId, pageNumber)
        return SttResult(documentId, pageNumber, listOf("테스트입니다", "테스트지롱", "나는야 테스트"), listOf(2,5,8), listOf(3,4,7), System.currentTimeMillis())
    }
}