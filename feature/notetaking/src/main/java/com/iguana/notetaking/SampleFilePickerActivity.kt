package com.iguana.notetaking

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
//import com.iguana.notetaking.PdfViewerActivity


class SampleFilePickerActivity : AppCompatActivity() {
    private lateinit var openPdfLauncher: ActivityResultLauncher<Array<String>>
    private val viewModel: SampleFilePickerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sample_file_picker)

        _createSamplePdf()

        // 파일 선택기 등록
        openPdfLauncher =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
                if (uri != null) {
                    // PDF 파일 처리 로직 호출
                    viewModel.handlePdf(uri)

                    // PDF 파일의 이름을 정확히 가져오기
                    val fileName = viewModel.getFileName(this, uri)
                    Log.d("PDF_INFO", "선택된 파일: $fileName")

                    // PDF 메타데이터 가져오기 (내장된 파일디스크립터 사용시)
                    viewModel.getPdfMetadata(this, uri)
                    // PDF 메타데이터 가져오기 (iText 외부 라이브러리 사용시 )
                    viewModel.getAdvancedPdfMetadata(this, uri)

                    // 선택된 파일을 PDFBox Activity 로 전달
                    val intent = Intent(this, PDFBoxActivity::class.java)
                    intent.putExtra("PDF_URI", uri.toString())  // Uri를 전달
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "PDF 선택 취소됨", Toast.LENGTH_SHORT).show()
                }
            }

        val layout = findViewById<LinearLayout>(R.id.addButton)
        layout.setOnClickListener {
            // 파일 선택기 실행
            openPdfLauncher.launch(arrayOf("application/pdf"))
        }
    }

    private fun _createSamplePdf() {
        viewModel.createSamplePdf()
    }
}