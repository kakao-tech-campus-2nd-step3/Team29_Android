package com.iguana.notetaking

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import com.iguana.notetaking.databinding.ActivitySampleFilePickerBinding


class SampleFilePickerActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySampleFilePickerBinding
    private lateinit var openPdfLauncher: ActivityResultLauncher<Array<String>>
    private val viewModel: SampleFilePickerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySampleFilePickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createSamplePdf()

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

                    // 선택된 파일을 NotetakingActivity Activity 로 전달
                    val intent = Intent(this, NotetakingActivity::class.java)
                    intent.putExtra("PDF_URI", uri.toString())  // Uri를 전달
                    intent.putExtra("PDF_TITLE", fileName)  // 파일 이름을 전달
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "PDF 선택 취소됨", Toast.LENGTH_SHORT).show()
                }
            }

        binding.addFileButton?.setOnClickListener {
            // 파일 선택기 실행
            openPdfLauncher.launch(arrayOf("application/pdf"))
        }
    }

    private fun createSamplePdf() {
        viewModel.createSamplePdf()
    }
}