package com.iguana.notetaking

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.rendering.PDFRenderer
import java.io.InputStream


class PDFBoxActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pdfbox)

        // PDFBox 리소스 로더 초기화
        PDFBoxResourceLoader.init(applicationContext)

        // Intent에서 PDF URI 받기
        val pdfUriString = intent.getStringExtra("PDF_URI")
        if (pdfUriString != null) {
            val pdfUri = Uri.parse(pdfUriString)
            renderFile(pdfUri)
        }


    }

    // PDF 파일의 모든 페이지를 렌더링하는 함수
    fun renderFile(uri: Uri) {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        if (inputStream != null) {
            // PDF 파일을 로드
            val document: PDDocument = PDDocument.load(inputStream) // PDF 문서
            val renderer = PDFRenderer(document) // PDF 렌더러
            val pageCount = document.numberOfPages // 페이지 수

            // PDF 페이지들을 담을 레이아웃
            val scrollView = findViewById<ScrollView>(R.id.scrollView)
            val linearLayout = findViewById<LinearLayout>(R.id.pdfLinearLayout)

            // 모든 페이지를 순차적으로 렌더링
            for (i in 0 until pageCount) {
                val bitmap: Bitmap = renderer.renderImageWithDPI(i, 300f) // Render at 300 DPI

                // 각 페이지마다 ImageView 생성
                val imageView = ImageView(this)
                imageView.layoutParams = LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
                )
                imageView.setImageBitmap(bitmap)

                // ImageView를 LinearLayout에 추가
                linearLayout.addView(imageView)
            }

            // 리소스 해제
            document.close()
            inputStream.close()
        } else {
            Toast.makeText(this, "PDF 파일을 열 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}

