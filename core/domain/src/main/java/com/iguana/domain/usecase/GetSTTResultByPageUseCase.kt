package com.iguana.domain.usecase

import com.iguana.domain.model.record.SttItem
import com.iguana.domain.model.record.SttResult
import com.iguana.domain.repository.SttRepository
import javax.inject.Inject

class GetSTTResultByPageUseCase @Inject constructor(
    private val sttRepository: SttRepository
) {
    suspend operator fun invoke(documentId: Long, pageNumber: Int): SttResult {
        // TODO: 아래 데이터는 임시 데이터로 서버 구현 시 주석 해제
//        return sttRepository.getSTTResult(documentId, pageNumber)

        // 더미 데이터 생성
        val dummySttItems = listOf(
            SttItem(startTime = 2, endTime = 3, content = "테스트입니다 테스트입니다 테스트입니다 테스트입니다 테스트입니다 테스트입니다 테스트입니다 테스트입니다 테스트입니다 테스트입니다 테스트입니다 테스트입니다 테스트입니다 테스트입니다 테스트입니다 테스트입니다 테스트입니다 테스트입니다"),
            SttItem(startTime = 4, endTime = 6, content = "테스트지롱 테스트지롱 테스트지롱 테스트지롱 테스트지롱 테스트지롱 테스트지롱 테스트지롱"),
            SttItem(startTime = 8, endTime = 9, content = "나는야 테스트 나는야 테스트입니다 테스트입니다 테스트입니다 테스트입니다 테입니다 테스트입니다 테스트입니다 테스트입니다 테입니다 테스트입니다 테스트입니다 테스트입니다 테입니다 테스트입니다 테스트입니다 테스트입니다 테입니다 테스트입니다 테스트입니다 테스트입니다 테입니다 테스트입니다 테스트입니다 테스트입니다 테입니다 테스트입니다 테스트입니다 테스트입니다 테입니다 테스트입니다 테스트입니다 테스트입니다 테입니다 테스트입니다 테스트입니다 테스트입니다 테입니다 테스트입니다 테스트입니다 테스트입니다 테입니다 테스트입니다 테스트입니다 테스트입니다 테입니다 테스트입니다 테스트입니다 테스트입니다 테")
        )

        return SttResult(
            documentId = documentId,
            pageNumber = pageNumber,
            sttContents = dummySttItems,
            receivedTime = System.currentTimeMillis()
        )
    }
}