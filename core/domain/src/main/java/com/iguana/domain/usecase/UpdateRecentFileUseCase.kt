package com.iguana.domain.usecase

import com.iguana.domain.repository.RecentFileRepository
import javax.inject.Inject

class UpdateRecentFileUseCase  @Inject constructor(
    private val recentFileRepository: RecentFileRepository
) {
    operator fun invoke(id: Long, fileName: String) {
        recentFileRepository.updateFileName(id, fileName)
    }
}