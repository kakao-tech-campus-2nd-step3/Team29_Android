package com.iguana.domain.usecase

import com.iguana.domain.repository.DocumentsRepository
import javax.inject.Inject

class DeleteFolderUseCase @Inject constructor(
    private val documentsRepository: DocumentsRepository
) {
    suspend operator fun invoke(folderId: Long) {
        return documentsRepository.deleteFolder(folderId)
    }
}