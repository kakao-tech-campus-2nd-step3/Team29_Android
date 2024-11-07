package com.iguana.domain.usecase

import com.iguana.domain.model.Document
import com.iguana.domain.repository.DocumentsRepository
import javax.inject.Inject

class UpdateDocumentNameUseCase @Inject constructor(
    private val documentsRepository: DocumentsRepository
) {
    suspend operator fun invoke(folderId: Long, documentId: Long, newName: String): Result<Document> {
        return documentsRepository.updateDocumentName(folderId, documentId, newName)
    }
} 