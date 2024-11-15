package com.iguana.data.repository

import com.iguana.data.mapper.toDomain
import com.iguana.data.remote.api.SttApi
import com.iguana.domain.model.record.SttResult
import com.iguana.domain.model.record.SttStatusResultByPage
import com.iguana.domain.repository.SttRepository
import javax.inject.Inject

class SttRepositoryImpl @Inject constructor(
    private val sttApi: SttApi
): SttRepository {
    override suspend fun getSTTResult(documentId: Long, pageNumber: Int): SttResult {
        return sttApi.getSTTResult(documentId, pageNumber).toDomain(documentId)
    }

    override suspend fun getSTTStatus(documentId: Long, pageNumber: Int): SttStatusResultByPage {
        return sttApi.getSTTStatus(documentId, pageNumber).toDomain(documentId)
    }
}