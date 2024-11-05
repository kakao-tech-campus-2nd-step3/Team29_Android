package com.iguana.data.repository

import android.util.Log
import com.iguana.data.mapper.toDomain
import com.iguana.data.remote.api.SummarizeApi
import com.iguana.data.remote.model.SummarizeRequestDto
import com.iguana.domain.model.ai.AIResult
import com.iguana.domain.model.ai.AIStatusResult
import com.iguana.domain.model.ai.AIStatusResultByPage
import com.iguana.domain.repository.AIRepository
import com.iguana.domain.utils.AppError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AIRepositoryImpl @Inject constructor(
    private val summarizeApi: SummarizeApi
): AIRepository {
    override suspend fun requestSummarization(documentId: Long, pages: List<Int>) {
        summarizeApi.requestSummarization(SummarizeRequestDto(documentId, pages))
    }

    override suspend fun checkStatus(documentId: Long): AIStatusResult {
        return summarizeApi.checkStatus(documentId).toDomain()
    }

    override suspend fun checkStatusByPage(
        documentId: Long,
        pageNumber: Int
    ): AIStatusResultByPage {
        return summarizeApi.checkStatusByPage(documentId, pageNumber).toDomain()
    }

    override suspend fun getSummarization(documentId: Long): List<AIResult> {
        return summarizeApi.getSummarization(documentId).toDomain()
    }

    override suspend fun getSummarizationByPage(
        documentId: Long,
        pageNumber: Int
    ): AIResult {
        return summarizeApi.getSummarizationByPage(documentId, pageNumber).toDomain(documentId, pageNumber)
    }
}