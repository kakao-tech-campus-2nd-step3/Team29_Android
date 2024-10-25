package com.iguana.domain.usecase

import com.iguana.domain.model.FolderContent
import com.iguana.domain.repository.DocumentsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllDocumentsUseCase @Inject constructor(
    private val documentsRepository: DocumentsRepository
) {
    suspend operator fun invoke(): Result<FolderContent> {
        return documentsRepository.getAllDocuments()
    }
}