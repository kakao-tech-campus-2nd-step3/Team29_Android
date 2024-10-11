package com.iguana.domain.usecase

import com.iguana.domain.repository.DocumentsRepository
import com.iguana.domain.model.Document
import javax.inject.Inject

class UpdateDocumentNameUseCase @Inject constructor(
    private val documentsRepository: DocumentsRepository
) {
    suspend operator fun invoke(documentId: Long, newName: String): Result<Document> {
        return documentsRepository.updateDocumentName(documentId, newName)
    }
}