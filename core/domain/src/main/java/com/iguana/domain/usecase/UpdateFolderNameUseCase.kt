package com.iguana.domain.usecase


import com.iguana.domain.model.FolderContentItem
import com.iguana.domain.repository.DocumentsRepository
import javax.inject.Inject

class UpdateFolderNameUseCase @Inject constructor(
    private val documentsRepository: DocumentsRepository
) {
    suspend operator fun invoke(folderId: Long, newName: String): FolderContentItem? {
        return documentsRepository.updateFolderName(folderId, newName)
    }
}