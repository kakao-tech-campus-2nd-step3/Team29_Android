package com.iguana.notetaking

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets.Side
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.iguana.notetaking.databinding.ActivityNotetakingBinding
import com.iguana.notetaking.recording.RecordFragment
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
        observeToolbar()
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
        binding.toolbar.btnRecord.setOnClickListener {
            requestAudioPermissions()
            viewModel.toggleRecordTabActive()
        }
        binding.toolbar.btnAI.setOnClickListener {
            viewModel.toggleAITabActive()
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

    // 뷰모델의 상태를 관찰하여 UI 업데이트
    private fun observeToolbar() {
        // Record 탭의 활성화 상태 관찰
        viewModel.isRecordActive.observe(this) { isActive ->
            binding.toolbar.btnRecord.isSelected = isActive // 선택된 상태로 업데이트
            if (isActive) {
                startRecordingInFragment()
                Toast.makeText(this, "녹음이 시작되었습니다.", Toast.LENGTH_SHORT).show()
            } else if (viewModel.isRecordingStopped()) {
                stopRecordingInFragment()
                Toast.makeText(this, "녹음이 종료되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // AI 탭의 활성화 상태 관찰
        viewModel.isAIActive.observe(this) { isActive ->
            binding.toolbar.btnAI.isSelected = isActive // 선택된 상태로 업데이트
        }
    }

    private val RECORD_AUDIO_PERMISSION_REQUEST_CODE = 1001

    // 권한 요청 메서드
    private fun requestAudioPermissions() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_PERMISSION_REQUEST_CODE)
        } else {
            // 이미 권한이 있는 경우
            Log.d("NotetakingActivity", "이미 녹음 권한이 있습니다.")
        }
    }

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECORD_AUDIO_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this, "녹음 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // 녹음 시작 메서드
    private fun startRecordingInFragment() {
        val sideBarFragment = getSideBarFragment() // SidebarFragment 가져오기
        sideBarFragment?.startRecordingInRecordFragment() // SidebarFragment에 녹음 시작 요청
    }

    // 녹음 중지 메서드
    private fun stopRecordingInFragment() {
        val sideBarFragment = getSideBarFragment() // SidebarFragment 가져오기
        sideBarFragment?.stopRecordingInRecordFragment() // SidebarFragment에 녹음 중지 요청
    }
}
