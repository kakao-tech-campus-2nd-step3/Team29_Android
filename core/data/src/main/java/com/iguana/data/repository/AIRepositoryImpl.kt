package com.iguana.data.repository

import com.iguana.data.mapper.toDomain
import com.iguana.data.remote.api.SummarizeApi
import com.iguana.data.remote.model.SummarizeRequestDto
import com.iguana.domain.model.ai.AIResult
import com.iguana.domain.model.ai.AIStatusResult
import com.iguana.domain.repository.AIRepository
import com.iguana.domain.utils.AppError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AIRepositoryImpl @Inject constructor(
    private val summarizeApi: SummarizeApi
): AIRepository {
    override suspend fun requestSummarization(documentId: Long, pages: List<Int>): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                summarizeApi.requestSummarization(SummarizeRequestDto(documentId, pages))
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun checkStatus(documentId: Long): Result<AIStatusResult> {
        return withContext(Dispatchers.IO) {
            try {
                val response = summarizeApi.checkStatus(documentId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        return@withContext Result.success(it.toDomain()) // body가 null이 아니면 성공 반환
                    }
                    // body가 null일 경우 실패 처리
                    return@withContext Result.failure(AppError.NullResponseError("Response body is null"))
                }
                return@withContext Result.failure(AppError.UnknownError(response.message()))
            } catch (e: Exception) {
                // 네트워크 요청 실패 처리
                return@withContext Result.failure(AppError.NetworkError(e.hashCode()))
            }
        }
    }

    override suspend fun getSummarization(documentId: Long): Result<List<AIResult>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = summarizeApi.getSummarization(documentId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        return@withContext Result.success(it.toDomain())
                    }
                    return@withContext Result.failure(AppError.NullResponseError("Response body is null"))
                }
                return@withContext Result.failure(AppError.UnknownError(response.message()))

            }
            catch (e: Exception) {
                return@withContext Result.failure(AppError.NetworkError(e.hashCode()))
            }
        }
    }
}