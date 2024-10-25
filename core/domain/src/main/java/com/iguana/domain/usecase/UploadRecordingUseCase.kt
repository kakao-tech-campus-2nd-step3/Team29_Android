package com.iguana.domain.usecase

import com.iguana.domain.model.record.RecordingFile
import com.iguana.domain.repository.RecordRepository
import java.io.File
import javax.inject.Inject

class UploadRecordingUseCase @Inject constructor(
    private val recordRepository: RecordRepository
) {
    suspend operator fun invoke(
        documentId: Long,
        filePath: String,
        fileName: String
    ): RecordingFile {
        val recordingFile = createRecordingFile(documentId, filePath, fileName)
        return recordRepository.uploadRecordingFile(recordingFile)
    }

    // RecordingFile 객체 생성 메서드
    private fun createRecordingFile(
        documentId: Long,
        filePath: String,
        fileName: String
    ): RecordingFile {
        val file = File(filePath)
        return RecordingFile(
            filePath = filePath,
            fileSize = file.length(),
            format = "3gp",
            duration = 0L, // 녹음 길이 설정 필요 시 추가
            documentName = fileName,
            recordingId = null,
            documentId = documentId
        )
    }
}