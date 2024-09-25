package com.iguana.notetaking

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.view.ViewGroup.LayoutParams
import android.widget.ScrollView
import android.widget.Toast

class PdfViewerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pdfviewer)

        // Intent에서 PDF URI 받기
        val pdfUriString = intent.getStringExtra("PDF_URI")
        if (pdfUriString != null) {
            val pdfUri = Uri.parse(pdfUriString)
            openPdf(pdfUri)
        }
    }

    // PDF 파일의 모든 페이지를 렌더링하는 함수
    private fun openPdf(uri: Uri) {
        try {
            // PDF 파일을 열기 위해 ParcelFileDescriptor를 사용
            val fileDescriptor = contentResolver.openFileDescriptor(uri, "r")
            if (fileDescriptor != null) {
                val renderer = PdfRenderer(fileDescriptor)
                val pageCount = renderer.pageCount

                // ScrollView에서 PDF 페이지들을 담을 레이아웃
                val scrollView = findViewById<ScrollView>(R.id.scrollView)
                val linearLayout = findViewById<LinearLayout>(R.id.pdfLinearLayout)

                // 모든 페이지를 순차적으로 렌더링
                for (i in 0 until pageCount) {
                    val page = renderer.openPage(i)
                    val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                    // 각 페이지마다 ImageView 생성
                    val imageView = ImageView(this)
                    imageView.layoutParams = LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                    )
                    imageView.setImageBitmap(bitmap)

                    // ImageView를 LinearLayout에 추가
                    linearLayout.addView(imageView)

                    // 리소스 해제
                    page.close()
                }

                // 리소스 해제
                renderer.close()
                fileDescriptor.close()
            } else {
                Toast.makeText(this, "PDF 파일을 열 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
