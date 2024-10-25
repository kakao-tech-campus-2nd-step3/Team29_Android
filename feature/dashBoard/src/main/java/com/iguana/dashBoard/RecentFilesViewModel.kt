package com.iguana.dashBoard

import android.content.Context
import android.content.Intent
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iguana.domain.model.RecentFile
import com.iguana.domain.repository.RecentFileRepository
import com.iguana.domain.usecase.SaveFileInLocalUsecase
import com.iguana.domain.usecase.SaveFileInRemoteUsecase
import com.iguana.domain.usecase.SaveRecentFileUsecase
import com.iguana.notetaking.NotetakingActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RecentFilesViewModel @Inject constructor(
    private val recentFileRepository: RecentFileRepository,
    private val saveFileInLocalUseCase: SaveFileInLocalUsecase,
    private val saveFileInRemoteUseCase: SaveFileInRemoteUsecase,
    private val saveRecentFileUsecase: SaveRecentFileUsecase
) : ViewModel() {
    private val _recentFiles = MutableStateFlow<List<RecentFile>>(emptyList())
    val recentFiles: StateFlow<List<RecentFile>> = _recentFiles.asStateFlow()

    init {
        loadRecentFiles()
    }

    private fun loadRecentFiles() {
        viewModelScope.launch {
            recentFileRepository.getRecentFiles().collect { files ->
                _recentFiles.value = files
            }
        }
    }

    fun openFile(recentFile: RecentFile, context: Context) {
        viewModelScope.launch {
            recentFileRepository.insertRecentFile(
                recentFile.id,
                recentFile.fileName,
                recentFile.fileUri
            )
        }
        val intent = Intent(context, NotetakingActivity::class.java).apply {
            putExtra("PDF_URI", recentFile.fileUri)
            putExtra("PDF_TITLE", recentFile.fileName)
            putExtra("DOCUMENT_ID", recentFile.id.toString())
        }
        context.startActivity(intent)
    }

    fun updateBookmark(fileId: Long, bookmarkedPage: Int) {
        viewModelScope.launch {
            recentFileRepository.updateBookmark(fileId, bookmarkedPage)
        }
    }

    fun cleanupOldFiles(daysToKeep: Int = 30) {
        viewModelScope.launch {
            recentFileRepository.cleanupOldFiles(daysToKeep)
        }
    }


    // PDF 파일 선택 처리
    fun uploadPdf(uri: Uri?, context: Context) {
        if (uri != null) {
            Log.d("testt", "업로드 pdf 시작: $uri")
            val fileName = getFileName(context, uri)
            Log.d("testt", "fileName: $fileName")

            viewModelScope.launch {
                try {
                    Log.d("testt", "uri: $uri")
                    // 1. 로컬에 파일 저장 -> 내부 URI 리턴
                    val internalUri = saveFileInLocalUseCase.execute(uri, fileName)

                    // TODO: 서버 구현이 되면 아래 주석 해제
//                    if (internalUri != null) {
//                        // 2. 서버에 파일 업로드 - 서버 구현이 되면 주석 해제
//                        val result = saveFileInRemoteUseCase.execute(-1, internalUri, fileName)
//                        result.onSuccess { document ->
//                            // 3. Room 데이터베이스에 저장 (내부 URI 사용)
//                            document.url?.let {
//                                saveRecentFileUsecase.invoke(
//                                    document.id,
//                                    fileName,
//                                    it
//                                )
//                            }
//                            // 4. 선택된 파일을 NotetakingActivity로 전달
//                            val intent = Intent(context, NotetakingActivity::class.java).apply {
//                                putExtra("PDF_URI", document.url)
//                                putExtra("PDF_TITLE", fileName)
//                                putExtra("DOCUMENT_ID", document.id)
//                            }
//                            context.startActivity(intent)
//                        }.onFailure {
//                            Toast.makeText(context, "파일 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
//                        }
//                    else {
//                        Toast.makeText(context, "파일 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
//                    }

                    // TODO: 서버완료되면 아래 코드 삭제 후 위 코드 주석해제
                    val intent = Intent(context, NotetakingActivity::class.java).apply {
                        putExtra("PDF_URI", internalUri)
                        putExtra("PDF_TITLE", fileName)
                        // 시간으로 더미값 생성해서 넣기
                        putExtra("DOCUMENT_ID", System.currentTimeMillis().toString())
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "파일 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    // PDF 메타데이터 가져오기
    private fun getPdfMetadata(context: Context, uri: Uri) {
        val fileDescriptor: ParcelFileDescriptor? =
            context.contentResolver.openFileDescriptor(uri, "r")
        fileDescriptor?.use {
            val fileSize = File(uri.path!!).length()  // 파일 크기
            val fileName = File(uri.path!!).name      // 파일 이름
            val lastModified = File(uri.path!!).lastModified() // 마지막 수정일
            val renderer = PdfRenderer(it)
            val pageCount = renderer.pageCount     // 페이지 수

            // 로그에 PDF 정보 출력
            Log.d(
                "PDF_INFO",
                "파일 이름: $fileName, 파일 크기: $fileSize, 페이지 수: $pageCount, 마지막 수정일: $lastModified"
            )

            renderer.close()
        } ?: Toast.makeText(context, "PDF 파일을 열 수 없습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun getFileName(context: Context, uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result =
                        cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result ?: "unknown"
    }
}