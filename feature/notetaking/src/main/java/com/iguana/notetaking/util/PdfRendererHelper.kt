package com.iguana.notetaking.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.iguana.data.BuildConfig
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.rendering.PDFRenderer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

class PdfRendererHelper @Inject constructor(@ApplicationContext private val context: Context) {

    init {
        // PDFBox 리소스 로더 초기화
        PDFBoxResourceLoader.init(context)
    }
    private suspend fun downloadPdfToLocal(fileUrl: String): File? {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("PdfRendererHelper", "다운로드 URL: $fileUrl")
                val url = URL(fileUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()

                val responseCode = connection.responseCode
                Log.d("PdfRendererHelper", "HTTP 응답 코드: $responseCode")
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    val errorStream =
                        connection.errorStream?.bufferedReader()?.use { it.readText() }
                }

                val inputStream: InputStream = connection.inputStream
                val file = File(context.cacheDir, "downloaded_pdf.pdf")
                val outputStream = FileOutputStream(file)

                val buffer = ByteArray(1024)
                var length: Int
                while (inputStream.read(buffer).also { length = it } > 0) {
                    outputStream.write(buffer, 0, length)
                }

                outputStream.close()
                inputStream.close()
                connection.disconnect()

                Log.d("PdfRendererHelper", "파일 다운로드 성공: ${file.absolutePath}")
                file
            } catch (e: IOException) {
                Log.e("PdfRendererHelper", "IOException 발생: ${e.message}")
                e.printStackTrace()
                null
            } catch (e: SecurityException) {
                Log.e("PdfRendererHelper", "SecurityException 발생: ${e.message}")
                e.printStackTrace()
                null
            } catch (e: Exception) {
                Log.e("PdfRendererHelper", "예외 발생: ${e::class.java.simpleName} - ${e.message}")
                e.printStackTrace()
                null
            }
        }
    }

    // PDF 파일의 특정 페이지를 렌더링하여 반환
    suspend fun renderPage(uri: Uri, pageIndex: Int): Bitmap? {
        return try {
            // 렌더링 또는 파일 사용 시
            val fullUrl = "${BuildConfig.API_BASE_URL}/${uri}"
            val localFile = downloadPdfToLocal(fullUrl)
            if (localFile != null && localFile.exists()) {
                val document = PDDocument.load(localFile)
                val renderer = PDFRenderer(document)
                val bitmap = renderer.renderImageWithDPI(pageIndex, 150f) // DPI를 적절하게 조절
                document.close()
                localFile.delete() // 사용 후 파일 삭제
                bitmap
            } else {
                Log.e("PdfRendererHelper", "PDF 파일 다운로드 실패")
                null
            }
        } catch (e: Exception) {
            e.printStackTrace() // 에러 로그를 출력하여 문제 파악에 도움
            null
        }
    }

    suspend fun getPageCount(uri: Uri): Int {
        return try {
            val fullUrl = "${BuildConfig.API_BASE_URL}/${uri}"
            val localFile = downloadPdfToLocal(fullUrl)
            if (localFile != null && localFile.exists()) {
                val document = PDDocument.load(localFile)
                val pageCount = document.numberOfPages
                document.close()
                localFile.delete() // 사용 후 파일 삭제
                pageCount
            } else {
                Log.e("PdfRendererHelper", "PDF 파일 다운로드 실패")
                0
            }
        } catch (e: Exception) {
            e.printStackTrace() // 에러 로그를 출력하여 문제 파악에 도움
            0
        }
    }


}
