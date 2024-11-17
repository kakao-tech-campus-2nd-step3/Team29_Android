package com.iguana.domain.usecase

import com.iguana.domain.repository.RecentFileRepository
import javax.inject.Inject

class DeleteRecentFileUseCase @Inject constructor(
    private val recentFileRepository: RecentFileRepository
) {
    suspend operator fun invoke(id: Long) {
        recentFileRepository.deleteRecentFile(id)
    }
}