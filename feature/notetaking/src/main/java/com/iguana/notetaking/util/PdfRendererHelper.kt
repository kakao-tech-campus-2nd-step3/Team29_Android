package com.iguana.notetaking.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.rendering.PDFRenderer
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.InputStream
import javax.inject.Inject

class PdfRendererHelper @Inject constructor(@ApplicationContext private val context: Context) {

    init {
        // PDFBox 리소스 로더 초기화
        PDFBoxResourceLoader.init(context)
    }

    // PDF 파일의 특정 페이지를 렌더링하여 반환
    fun renderPage(uri: Uri, pageIndex: Int): Bitmap? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            inputStream?.use {
                val document = PDDocument.load(it)
                val renderer = PDFRenderer(document)
                val bitmap = renderer.renderImageWithDPI(pageIndex, 150f) // DPI를 적절하게 조절
                document.close()
                bitmap
            }
        } catch (e: Exception) {
            e.printStackTrace() // 에러 로그를 출력하여 문제 파악에 도움
            null
        }
    }

    fun getPageCount(uri: Uri): Int {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            inputStream?.use {
                val document = PDDocument.load(it)
                val pageCount = document.numberOfPages
                document.close()
                pageCount
            } ?: 0
        } catch (e: Exception) {
            e.printStackTrace() // 에러 로그를 출력하여 문제 파악에 도움
            0
        }
    }


}
