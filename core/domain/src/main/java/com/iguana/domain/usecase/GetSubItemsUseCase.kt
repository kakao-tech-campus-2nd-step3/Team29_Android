package com.iguana.domain.usecase

import com.iguana.domain.model.FolderContent
import com.iguana.domain.repository.DocumentsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSubItemsUseCase @Inject constructor(
    private val documentsRepository: DocumentsRepository
) {
    operator fun invoke(folderId: Long): Flow<FolderContent> {
        return documentsRepository.getSubItems(folderId)
    }
}