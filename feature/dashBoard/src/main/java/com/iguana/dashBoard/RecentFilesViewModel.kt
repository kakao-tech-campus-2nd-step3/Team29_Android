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
import com.iguana.data.local.entity.RecentFileEntity
import com.iguana.domain.model.RecentFile
import com.iguana.domain.repository.RecentFileRepository
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
    private val recentFileRepository: RecentFileRepository
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

    private fun addDummyData() {
        viewModelScope.launch {
            val dummyFiles = listOf(
                RecentFileEntity(
                    "1",
                    "바이오 빅데이터.pdf",
                    "file:///dummy/path1",
                    System.currentTimeMillis(),
                    1
                ),
                RecentFileEntity(
                    "2",
                    "기계학습.pdf",
                    "file:///dummy/path2",
                    System.currentTimeMillis() - 86400000,
                    3
                ),
                RecentFileEntity(
                    "3",
                    "유전체 정보학.pdf",
                    "file:///dummy/path3",
                    System.currentTimeMillis() - 172800000,
                    null
                ),
                RecentFileEntity(
                    "4",
                    "의생명 정보과학.pdf",
                    "file:///dummy/path4",
                    System.currentTimeMillis() - 259200000,
                    5
                )
            )
            dummyFiles.forEach {
                recentFileRepository.insertRecentFile(
                    it.id,
                    it.fileName,
                    it.fileUri
                )
            }
        }
    }


    fun openFile(id: String, fileName: String, fileUri: String) {
        viewModelScope.launch {
            recentFileRepository.insertRecentFile(id, fileName, fileUri)
        }
    }

    fun updateBookmark(fileId: String, bookmarkedPage: Int) {
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
    fun handlePdf(uri: Uri?, context: Context) {
        if (uri != null) {
            val fileName = getFileName(context, uri)

            viewModelScope.launch {
                recentFileRepository.insertRecentFile(
                    uri.toString(),
                    fileName,
                    uri.toString()
                ) // Save recent file
                getPdfMetadata(context, uri)

                // 선택된 파일을 NotetakingActivity로 전달
                val intent = Intent(context, NotetakingActivity::class.java).apply {
                    putExtra("PDF_URI", uri.toString())
                    putExtra("PDF_TITLE", fileName)
                }
                context.startActivity(intent)
            }
        }
    }

    // PDF 메타데이터 가져오기
    private fun getPdfMetadata(context: Context, uri: Uri) {
        val fileDescriptor: ParcelFileDescriptor? =
            context.contentResolver.openFileDescriptor(uri, "r")
        fileDescriptor?.use {
            val fileSize = File(uri.path).length()  // 파일 크기
            val fileName = File(uri.path).name      // 파일 이름
            val lastModified = File(uri.path).lastModified() // 마지막 수정일
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