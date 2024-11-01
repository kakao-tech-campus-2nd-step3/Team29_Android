package com.iguana.notetaking

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.iguana.notetaking.databinding.ActivityNotetakingBinding
import com.iguana.notetaking.pdf.PdfViewerFragment
import com.iguana.notetaking.sidebar.SideBarFragment
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class NotetakingActivity : AppCompatActivity() {

    companion object {
        const val PDF_URI_KEY = "PDF_URI"
        const val PDF_TITLE_KEY = "PDF_TITLE"
        const val DEFAULT_TITLE = "무제"
        const val DOCUMENT_ID_KEY = "DOCUMENT_ID"
        const val RECORD_AUDIO_PERMISSION_REQUEST_CODE = 1001
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
        observeViewModel()
    }

    // 뷰 초기화 메서드
    private fun initializeView() {
        viewModel.apply {
            // PDF URI, 제목, 문서 ID 설정
            pdfUri = intent.getStringExtra(PDF_URI_KEY).toString()
            pdfTitle = intent.getStringExtra(PDF_TITLE_KEY) ?: DEFAULT_TITLE
            documentId = intent.getLongExtra(DOCUMENT_ID_KEY, -1)
        }

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
        binding.toolbar.apply {
            btnText.setOnClickListener {
                viewModel.toggleTextMode()
                // TODO 주석이 pdf 뷰어에 표시되도록 하는 로직
            }
            btnRecord.setOnClickListener { handleRecordingPermissionAndToggle() }
            btnAI.setOnClickListener { viewModel.toggleAI() }
            }
        }

    // 타이틀바 설정
    private fun setupTitleBar() {
        binding.titleBar.apply {
            // 뒤로가기 버튼 클릭 시 액티비티 종료
            backButton.setOnClickListener {
                finish()
            }
            // PDF 제목 설정
            titleBar.text = viewModel.pdfTitle
        }
    }

    // PDF 및 사이드바 초기화 메서드
    private fun setupPdfViewerAndSidebar() {
        replaceFragment(R.id.pdf_fragment_container, PdfViewerFragment.newInstance(Uri.parse(viewModel.pdfUri)))
        replaceFragment(R.id.side_bar_container, SideBarFragment.newInstance(viewModel.documentId, viewModel.pageNumber.value ?: 0))
    }

    // 프래그먼트 교체 메서드
    private fun replaceFragment(containerId: Int, fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(containerId, fragment)
            .commit()
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

    // 뷰모델의 상태를 관찰하여 UI 업데이트
    private fun observeViewModel() {
        viewModel.isRecordingActive.observe(this) { isActive ->
            binding.toolbar.btnRecord.isSelected = isActive
            toastRecordingStatus(isActive)
        }

        viewModel.isAIActive.observe(this) { isActive ->
            binding.toolbar.btnAI.isSelected = isActive
        }

        viewModel.isTextMode.observe(this) { isTextMode ->
            val textEditBar = binding.root.findViewById<View>(R.id.text_edit_bar)
            textEditBar.visibility = if (isTextMode) View.VISIBLE else View.GONE
        }
    }

    private fun handleRecordingPermissionAndToggle() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            viewModel.toggleRecording()
        } else {
            requestAudioPermissions()
        }
    }

    private fun toastRecordingStatus(isActive: Boolean) {
        val message = if (isActive) {
            "녹음이 시작되었습니다."
        } else if (viewModel.isRecordingStopped()) {
            "녹음이 종료되었습니다."
        } else {
            null
        }
        message?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
    }

    // 권한 요청 메서드
    private fun requestAudioPermissions() {
        requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_PERMISSION_REQUEST_CODE)
    }

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECORD_AUDIO_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            viewModel.toggleRecording()
        } else {
            Toast.makeText(this, "녹음 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
