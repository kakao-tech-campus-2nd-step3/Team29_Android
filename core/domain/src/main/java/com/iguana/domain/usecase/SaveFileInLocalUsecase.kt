package com.iguana.domain.usecase
import android.net.Uri
import com.iguana.domain.repository.RecentFileRepository
import com.iguana.domain.utils.FileHelper
import javax.inject.Inject

class SaveFileInLocalUsecase @Inject constructor(
    private val fileHelper: FileHelper,
    private val recentFileRepository: RecentFileRepository
) {
    suspend fun execute(uri: Uri, fileName: String, fileId: Long) {
        // 파일을 내부 저장소에 복사
        val copiedUri = fileHelper.copyFileToInternalStorage(uri, fileName)

        // 복사된 URI를 Room에 저장
        copiedUri?.let {
            recentFileRepository.insertRecentFile(
                id = fileId,  // 서버에서 받은 파일 ID 사용
                fileName = fileName,
                fileUri = it.toString()
            )
        }
    }
}