package com.iguana.domain.repository

import com.iguana.domain.model.record.SttResult

interface SttRepository {
    suspend fun getSTTResult(documentId: Long, pageNumber: Int): SttResult
}
