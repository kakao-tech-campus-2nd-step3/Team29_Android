package com.iguana.domain.usecase

import android.net.Uri
import com.iguana.domain.repository.RecentFileRepository
import javax.inject.Inject

class SaveRecentFileUsecase @Inject constructor(
    private val recentFileRepository: RecentFileRepository
) {
    suspend operator fun invoke(id: Long, fileName: String, fileUri: String) {
        recentFileRepository.insertRecentFile(id, fileName, fileUri)
    }
}