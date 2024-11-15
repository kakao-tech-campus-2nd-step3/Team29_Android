package com.iguana.domain.repository

import com.iguana.domain.model.record.SttResult
import com.iguana.domain.model.record.SttStatusResultByPage

interface SttRepository {
    suspend fun getSTTResult(documentId: Long, pageNumber: Int): SttResult
    suspend fun getSTTStatus(documentId: Long, pageNumber: Int): SttStatusResultByPage
}
