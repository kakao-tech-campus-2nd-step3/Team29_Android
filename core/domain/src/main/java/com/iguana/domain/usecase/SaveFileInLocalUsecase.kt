package com.iguana.domain.usecase
import android.net.Uri
import com.iguana.domain.repository.RecentFileRepository
import com.iguana.domain.utils.FileHelper
import javax.inject.Inject

class SaveFileInLocalUsecase @Inject constructor(
    private val fileHelper: FileHelper,
) {
    suspend fun execute(uri: Uri, fileName: String): Uri? {
        // 파일을 내부 저장소에 복사 후 파일 경로를 반환
        return fileHelper.copyFileToInternalStorage(uri, fileName)
    }
}