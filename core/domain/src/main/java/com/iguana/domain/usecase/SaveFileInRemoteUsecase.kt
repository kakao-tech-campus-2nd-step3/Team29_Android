package com.iguana.domain.usecase

import android.net.Uri
import com.iguana.domain.model.Document
import com.iguana.domain.repository.DocumentsRepository
import java.io.File
import javax.inject.Inject


class SaveFileInRemoteUsecase @Inject constructor(
    private val documentsRepository: DocumentsRepository
) {
    suspend fun execute(folderId: Long, fileUri: Uri, fileName: String): Result<Document> {
        // 파일 객체 생성
        val file = File(fileUri.path ?: return Result.failure(Exception("파일 경로 오류")))
        // 레포지토리를 사용하여 파일 업로드
        return documentsRepository.uploadDocument(folderId, file)
    }
}