package com.iguana.notetaking

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.iguana.notetaking.databinding.ActivityNotetakingBinding
import com.iguana.notetaking.sidebar.SideBarFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NotetakingActivity : AppCompatActivity() {

    companion object {
        const val PDF_URI_KEY = "PDF_URI"
        const val PDF_TITLE_KEY = "PDF_TITLE"
        const val DEFAULT_TITLE = "무제"
        const val DOCUMENT_ID_KEY = "DOCUMENT_ID"
    }


    private lateinit var binding: ActivityNotetakingBinding
    private val viewModel: NotetakingViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNotetakingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        initializeView()
    }

    // 뷰 초기화 메서드
    private fun initializeView() {
        viewModel.pdfUri = intent.getStringExtra(PDF_URI_KEY).toString()
        viewModel.pdfTitle = intent.getStringExtra(PDF_TITLE_KEY) ?: DEFAULT_TITLE
        viewModel.documentId = intent.getLongExtra(DOCUMENT_ID_KEY, -1)

        setupTitleBar()
        setupToolbar()
        setupPdfViewerAndSidebar()
    }

    // 페이지 변경 시 호출되는 메서드
    fun onPageChanged(pageNumber: Int) {
        viewModel.setPageNumber(pageNumber)
        updateSidebarWithPage(pageNumber)
    }

    // 툴바 설정
    private fun setupToolbar() {
        binding.toolbar.btnText.setOnClickListener {
            val pdfViewerFragment = getPdfViewerFragment()
            pdfViewerFragment?.getCurrentPdfPageFragment()?.addTextBox()
        }
    }

    // 타이틀바 설정
    private fun setupTitleBar() {
        binding.titleBar.backButton.setOnClickListener {
            finish()
        }
        binding.titleBar.titleBar.text = viewModel.pdfTitle
    }

    // PDF 및 사이드바 초기화 메서드
    private fun setupPdfViewerAndSidebar() {
            val pdfUri = Uri.parse(viewModel.pdfUri)
            replaceFragment(R.id.pdf_fragment_container, PdfViewerFragment.newInstance(pdfUri))
            replaceFragment(
                R.id.side_bar_container,
                SideBarFragment.newInstance(viewModel.documentId, viewModel.pageNumber.value ?: 0)
            )
    }

    // 프래그먼트 교체 메서드
    private fun replaceFragment(containerId: Int, fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(containerId, fragment)
            .commit()
    }

    // PDF 에러 처리 메서드
    private fun handlePdfError(message: String) {
        Log.e("NotetakingActivity", message)
        Toast.makeText(this, "PDF 파일을 열 수 없습니다.", Toast.LENGTH_SHORT).show()
    }

    // PDF 뷰어 프래그먼트 가져오기 메서드
    private fun getPdfViewerFragment(): PdfViewerFragment? {
        return supportFragmentManager.findFragmentById(R.id.pdf_fragment_container) as? PdfViewerFragment
    }

    // 사이드바 프래그먼트 가져오기 메서드
    private fun getSideBarFragment(): SideBarFragment? {
        return supportFragmentManager.findFragmentById(R.id.side_bar_container) as? SideBarFragment
    }

    // 사이드바 업데이트 메서드
    private fun updateSidebarWithPage(pageNumber: Int) {
        val sideBarFragment = getSideBarFragment()
        sideBarFragment?.updatePageNumber(pageNumber)
    }
}
