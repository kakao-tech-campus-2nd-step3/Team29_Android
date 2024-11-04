package com.iguana.notetaking.pdf

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.iguana.notetaking.NotetakingViewModel
import com.iguana.notetaking.databinding.FragmentPdfPageBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PdfPageFragment : Fragment(), PdfEditorListener {

    private val pdViewerViewModel: PdfViewerViewModel by viewModels()
    private val pdfPageViewModel: PdfPageViewModel by viewModels()
    private val sharedViewModel: NotetakingViewModel by activityViewModels()
    private var _binding: FragmentPdfPageBinding? = null
    private val binding get() = _binding!!
    private lateinit var pdfEditor: PdfEditor

    companion object {
        private const val ARG_PDF_URI = "PDF_URI"
        private const val ARG_PAGE_INDEX = "PAGE_INDEX"

        fun newInstance(pdfUri: Uri, pageIndex: Int): PdfPageFragment {
            val fragment = PdfPageFragment()
            val args = Bundle()
            args.putString(ARG_PDF_URI, pdfUri.toString())
            args.putInt(ARG_PAGE_INDEX, pageIndex)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // XML 레이아웃 파일을 인플레이트하여 반환
        _binding = FragmentPdfPageBinding.inflate(inflater, container, false)
        pdfEditor = PdfEditor(requireContext(), this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pdfUriString = arguments?.getString(ARG_PDF_URI)
        val pageIndex = arguments?.getInt(ARG_PAGE_INDEX, 0) ?: 0

        // 주석을 로드하기 전에 화면에 있던 기존 주석을 제거
        clearAnnotationsOnPage()
        // 현재 페이지 주석 로드
        pdfPageViewModel.loadAnnotations(sharedViewModel.documentId, pageIndex)

        if (pdfUriString != null) {
            val pdfUri = Uri.parse(pdfUriString)
            viewLifecycleOwner.lifecycleScope.launch {
                val bitmap = pdViewerViewModel.renderPage(pdfUri, pageIndex)
                bitmap?.let {
                    // PhotoView에 이미지 설정 (확대/축소 기능)
                    binding.photoView.setImageBitmap(it)
                }
            }
        }

        // 텍스트 박스 외부 클릭시 텍스트 모드 해제
        binding.pdfEditorView.setOnClickListener {
            sharedViewModel.setTextMode(false)
        }
    }


    // 새로운 텍스트 상자 추가
    fun addNewTextBox(pageIndex: Int) {
        val editText = pdfEditor.addTextBox(binding.pdfEditorView)
        pdfEditor.enableTextBoxEditing(editText) // 편집 모드 설정
        // EditText의 위치와 크기 정보를 기반으로 Annotation 객체 생성
        val annotation = com.iguana.domain.model.Annotation(
            content = editText.text.toString(),
            x = editText.x,
            y = editText.y,
            width = editText.width.toFloat(),
            height = editText.height.toFloat(),
            pageNumber = pageIndex
        )
        // 주석을 저장하도록 ViewModel 호출
        pdfPageViewModel.saveAnnotation(sharedViewModel.documentId, annotation, pageIndex)
    }

    // 주석 제거
    private fun clearAnnotationsOnPage() {
        pdfPageViewModel.clearAnnotations()
        binding.pdfEditorView.removeAllViews() // pdfEditorView 내 모든 뷰 제거
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDrag(dragging: Boolean) {
        (parentFragment as? PdfViewerFragment)?.setPagingEnabled(!dragging)
    }

    override fun onTextBoxClick(focused: Boolean) {
        if (focused) {
            sharedViewModel.setTextMode(true)
        }
    }

}
