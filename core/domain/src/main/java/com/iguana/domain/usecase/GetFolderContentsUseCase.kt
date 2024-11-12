package com.iguana.domain.usecase

import com.iguana.domain.model.FolderContent
import com.iguana.domain.repository.DocumentsRepository
import javax.inject.Inject

class GetFolderContentsUseCase @Inject constructor(
    private val repository: DocumentsRepository
) {
    suspend operator fun invoke(folderId: Long): Result<FolderContent> {
        return repository.getFolderContents(folderId)
    }
}