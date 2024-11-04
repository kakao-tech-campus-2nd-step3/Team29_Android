package com.iguana.domain.usecase

import com.iguana.domain.model.FolderContent
import com.iguana.domain.repository.DocumentsRepository
import javax.inject.Inject

class GetFolderContentsUseCase @Inject constructor(
    private val documentsRepository: DocumentsRepository
) {
    suspend operator fun invoke(folderId: Long, page: Int, size: Int, sortBy: String, sortDirection: String): Result<FolderContent> {
        return documentsRepository.getFolderContents(folderId, page, size, sortBy, sortDirection)
    }
}