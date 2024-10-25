package com.iguana.domain.usecase

import com.iguana.domain.model.record.RecordingFile
import com.iguana.domain.repository.RecordRepository
import javax.inject.Inject

class UploadRecordingUseCase @Inject constructor(
    private val recordRepository: RecordRepository
) {
    suspend operator fun invoke(documentId: Long, recordingFile: RecordingFile): RecordingFile {
        return recordRepository.uploadRecordingFile(recordingFile)
    }
}