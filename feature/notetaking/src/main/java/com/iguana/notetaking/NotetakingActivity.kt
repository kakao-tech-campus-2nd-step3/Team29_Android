package com.iguana.notetaking

import android.Manifest
import android.app.AlertDialog
import android.app.StatusBarManager
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.viewpager2.widget.ViewPager2
import com.iguana.notetaking.databinding.ActivityNotetakingBinding
import com.iguana.notetaking.pdf.PdfPageFragment
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
        window.statusBarColor = Color.WHITE
        WindowCompat.setDecorFitsSystemWindows(window, false)

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
                if (viewModel.isTextMode.value == true) {
                    addTextToCurrentPage()
                }
            }
            btnRecord.setOnClickListener { handleRecordingPermissionAndToggle() }
            btnAI.setOnClickListener {
                // AI 버튼이 활성화 상태로 잠깐 변경
                binding.toolbar.btnAI.isSelected = true

                // AI 버튼의 토글 상태를 ViewModel에 전달 (필요 시)
                viewModel.toggleAI()

                binding.toolbar.btnAI.postDelayed({
                    binding.toolbar.btnAI.isSelected = false
                }, 300)
            }
        }
        binding.textEditBar.apply {
            llTextFormatIcons.ivDelete.setOnClickListener { onDeleteAnnotationClick() }
            llTextFormatIcons.ivBold.setOnClickListener { showServicePreparingDialog() }
            llTextFormatIcons.ivItalic.setOnClickListener { showServicePreparingDialog() }
            llTextFormatIcons.ivUnderline.setOnClickListener { showServicePreparingDialog() }
            llTextFormatIcons.ivStrikeThrough.setOnClickListener { showServicePreparingDialog() }
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
        replaceFragment(
            R.id.pdf_fragment_container,
            PdfViewerFragment.newInstance(Uri.parse(viewModel.pdfUri))
        )
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


    private fun getPdfViewerFragment(): PdfViewerFragment? {
        return supportFragmentManager.findFragmentById(R.id.pdf_fragment_container) as? PdfViewerFragment
    }

    // 현재 페이지에 텍스트 추가 요청을 전달하기 위한 메서드
    private fun addTextToCurrentPage() {
        val pdfViewerFragment = getPdfViewerFragment()
        val currentPageFragment = pdfViewerFragment?.getCurrentPdfPageFragment()
        currentPageFragment?.addNewTextBox(viewModel.pageNumber.value ?: 0)
    }

    // 삭제 버튼 클릭 시 호출되는 메서드
    private fun onDeleteAnnotationClick() {
        val pdfViewerFragment = getPdfViewerFragment()
        val currentPageFragment = pdfViewerFragment?.getCurrentPdfPageFragment()
        currentPageFragment?.deleteSelectedTextBox()
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
            if (isActive) {
                viewModel.setActiveTab(0)
            }
            viewModel.showSideBar()
        }

        viewModel.isTextMode.observe(this) { isTextMode ->
            val textEditBar = binding.root.findViewById<View>(R.id.text_edit_bar)
            textEditBar.visibility = if (isTextMode) View.VISIBLE else View.GONE
        }

        viewModel.isAIActive.observe(this) { isActive ->
            binding.toolbar.btnAI.isSelected = isActive
            if (isActive) {
                viewModel.setActiveTab(1) // AI 탭 활성화 (1이 AI 탭이라고 가정)
            }
            viewModel.showSideBar()
        }

        viewModel.isSideBarVisible.observe(this) { isVisible ->
            binding.sideBarContainer.visibility = if (isVisible) View.VISIBLE else View.GONE
        }

        viewModel.activeTab.observe(this) { tab ->
            val sideBarFragment = getSideBarFragment()
            sideBarFragment?.setTab(tab) // SideBarFragment에 탭 설정 메서드를 추가
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
        requestPermissions(
            arrayOf(Manifest.permission.RECORD_AUDIO),
            RECORD_AUDIO_PERMISSION_REQUEST_CODE
        )
    }

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECORD_AUDIO_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            viewModel.toggleRecording()
        } else {
            Toast.makeText(this, "녹음 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showServicePreparingDialog() {
        val dialogView = LayoutInflater.from(this).inflate(com.iguana.designsystem.R.layout.dialog_service_preparing, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)

        val dialog = builder.create()

        // "확인" 버튼 클릭 시 다이얼로그 닫기
        dialogView.findViewById<Button>(com.iguana.designsystem.R.id.btnClose).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
