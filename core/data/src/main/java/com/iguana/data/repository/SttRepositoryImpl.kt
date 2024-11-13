package com.iguana.data.repository

import com.iguana.data.mapper.toDomain
import com.iguana.data.remote.api.SttApi
import com.iguana.domain.model.record.SttResult
import com.iguana.domain.repository.SttRepository

class SttRepositoryImpl(
    private val sttApi: SttApi
) : SttRepository {
    override suspend fun getSTTResult(documentId: Long, pageNumber: Int): SttResult {
        return sttApi.getSTTResult(documentId, pageNumber).toDomain(documentId)
    }
}