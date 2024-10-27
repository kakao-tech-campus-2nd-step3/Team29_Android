package com.iguana.domain.usecase

import com.iguana.domain.repository.RecordRepository
import javax.inject.Inject

class DeleteRecordingUseCase @Inject constructor(
    private val recordRepository: RecordRepository
) {
    suspend operator fun invoke(filePath: String) {
        recordRepository.deleteRecordingFile(filePath)
    }
}