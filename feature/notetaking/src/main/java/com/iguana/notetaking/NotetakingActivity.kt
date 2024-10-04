package com.iguana.notetaking

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.iguana.notetaking.databinding.ActivityNotetakingBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NotetakingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotetakingBinding
    private val viewModel: NotetakingViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNotetakingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // Intent에서 PDF URI 받기
        val pdfUriString = intent.getStringExtra("PDF_URI")
        val pdfTitle = intent.getStringExtra("PDF_TITLE") ?: "무제"

        binding.titleBar.titleBar.text = pdfTitle
        // 뒤로가기 버튼 클릭 리스너 설정
        binding.titleBar.backButton.setOnClickListener {
            finish()
        }

        // PDF URI가 있는 경우 프래그먼트 추가
        if (pdfUriString != null) {
            val pdfUri = Uri.parse(pdfUriString)
            val pdfViewerFragment = PdfViewerFragment.newInstance(pdfUri)

            supportFragmentManager.beginTransaction()
                .replace(R.id.pdf_fragment_container, pdfViewerFragment)
                .commit()

            val sideBarFragment = SideBarFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.side_bar_container, sideBarFragment)
                .commit()

            // 툴바의 텍스트 버튼 클릭 리스너 설정
            binding.toolbar.btnText.setOnClickListener {
                pdfViewerFragment.getCurrentPdfPageFragment()?.addTextBox()
            }
        } else {
            Log.e("NotetakingActivity", "PDF URI is null in Activity") // URI가 null인 경우 로그
        }
    }
}