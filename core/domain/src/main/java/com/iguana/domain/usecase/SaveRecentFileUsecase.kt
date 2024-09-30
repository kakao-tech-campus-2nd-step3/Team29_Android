package com.iguana.domain.usecase

import com.iguana.domain.repository.RecentFileRepository
import javax.inject.Inject

class SaveRecentFileUsecase @Inject constructor(
    private val recentFileRepository: RecentFileRepository
) {
    suspend operator fun invoke(id: String, fileName: String, fileUri: String) {
        recentFileRepository.insertRecentFile(id, fileName, fileUri)
    }
}