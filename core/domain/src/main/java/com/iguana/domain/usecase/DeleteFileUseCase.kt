package com.iguana.domain.usecase

import com.iguana.domain.repository.DocumentsRepository
import javax.inject.Inject

class DeleteFileUseCase @Inject constructor(
    private val documentsRepository: DocumentsRepository
) {
    suspend operator fun invoke(fileId: Long): Result<Unit> {
        return documentsRepository.deleteDocument(fileId)
    }
}