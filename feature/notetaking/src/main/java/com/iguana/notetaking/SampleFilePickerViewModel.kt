package com.iguana.notetaking

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.itextpdf.kernel.pdf.PdfReader
import java.io.File
import java.io.FileOutputStream


class SampleFilePickerViewModel : ViewModel() {
    private val _pdfUri = MutableLiveData<Uri?>()
    val pdfUri: LiveData<Uri?> get() = _pdfUri

    // PDF 파일 선택 처리
    fun handlePdf(uri: Uri?) {
        if (uri != null) {
            _pdfUri.value = uri
        }
    }
    fun createSamplePdf() {
        // PDFDocument 객체 생성
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)

        // PDF 페이지에 텍스트 추가
        val canvas = page.canvas
        val paint = Paint()
        paint.textSize = 50f
        canvas.drawText("샘플 PDF예요", 90f, 50f, paint)
        canvas.drawText("샘플 PDF예요2", 90f, 120f, paint)
        canvas.drawText("샘플 PDF예요3", 90f, 200f, paint)

        document.finishPage(page)

        // 에뮬레이터의 Download 폴더에 저장
        val directory = File(Environment.getExternalStorageDirectory().toString() + "/Download")
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory, "sample.pdf")
        document.writeTo(FileOutputStream(file))
        document.close()
    }

    // PDF 메타데이터 가져오기 (안드로이드 내장된 파일디스크립터 사용시)
    fun getPdfMetadata(context: Context, uri: Uri) {
        val fileDescriptor: ParcelFileDescriptor? = context.contentResolver.openFileDescriptor(uri, "r")
        fileDescriptor?.use {
            val fileSize = File(uri.path).length()  // 파일 크기
            val fileName = File(uri.path).name      // 파일 이름
            val renderer = PdfRenderer(it)
            val pageCount = renderer.pageCount     // 페이지 수

            // 로그에 PDF 정보 출력
            Log.d("PDF_INFO", "파일 이름: $fileName, 파일 크기: $fileSize, 페이지 수: $pageCount")

            renderer.close()
        } ?: Toast.makeText(context, "PDF 파일을 열 수 없습니다.", Toast.LENGTH_SHORT).show()
    }
    fun getAdvancedPdfMetadata(context: Context, uri: Uri) {
        val inputStream = context.contentResolver.openInputStream(uri)
        val pdfDoc = com.itextpdf.kernel.pdf.PdfDocument(PdfReader(inputStream))

        // PDF 메타데이터 가져오기
        val title = pdfDoc.documentInfo.title
        val author = pdfDoc.documentInfo.author
        val subject = pdfDoc.documentInfo.subject
        val keywords = pdfDoc.documentInfo.keywords
        val creationDate = pdfDoc.documentInfo.getMoreInfo("CreationDate")
        val modificationDate = pdfDoc.documentInfo.getMoreInfo("ModDate")
        val producer = pdfDoc.documentInfo.producer

        // 로그에 출력
        Log.d("PDF_INFO", "(iText 사용해서 받아온 메타데이터) 제목: $title, 저자: $author, 주제: $subject, 키워드: $keywords")
        Log.d("PDF_INFO", "(iText 사용해서 받아온 메타데이터) 생성일: $creationDate, 수정일: $modificationDate, 생성 소프트웨어: $producer")

        pdfDoc.close()
    }

    fun getFileName(context: Context, uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
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