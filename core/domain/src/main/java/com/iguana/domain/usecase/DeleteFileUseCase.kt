package com.iguana.domain.usecase

import com.iguana.domain.repository.DocumentsRepository
import javax.inject.Inject

class DeleteFileUseCase @Inject constructor(
    private val documentsRepository: DocumentsRepository
) {
    suspend operator fun invoke(folderId: Long, fileId: Long) {
        return documentsRepository.deleteDocument(folderId, fileId)
    }
}