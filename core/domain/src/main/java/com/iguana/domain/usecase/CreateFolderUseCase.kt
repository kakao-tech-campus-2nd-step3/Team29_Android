package com.iguana.domain.usecase

import com.iguana.domain.model.Folder
import com.iguana.domain.repository.DocumentsRepository
import javax.inject.Inject

class CreateFolderUseCase @Inject constructor(
    private val documentRepository: DocumentsRepository
) {
    suspend fun execute(parentFolderId: Long, folderName: String): Result<Folder> {
        return documentRepository.createFolder(parentFolderId, folderName)
    }
}