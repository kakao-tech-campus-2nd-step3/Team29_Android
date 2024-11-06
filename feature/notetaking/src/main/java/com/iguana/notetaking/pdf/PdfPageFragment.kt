package com.iguana.notetaking.pdf

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.iguana.notetaking.NotetakingViewModel
import com.iguana.notetaking.databinding.FragmentPdfPageBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PdfPageFragment : Fragment(), AnnotationListener {

    private val pdViewerViewModel: PdfViewerViewModel by viewModels()
    private val pdfPageViewModel: PdfPageViewModel by viewModels()
    private val sharedViewModel: NotetakingViewModel by activityViewModels()
    private var _binding: FragmentPdfPageBinding? = null
    private val binding get() = _binding!!
    private lateinit var annotationEditor: AnnotationEditor

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
        annotationEditor = AnnotationEditor(requireContext(), this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pdfUriString = arguments?.getString(ARG_PDF_URI)
        val pageIndex = arguments?.getInt(ARG_PAGE_INDEX, 0) ?: 0


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
            Log.d("AnnotationEditor", "Clicked on PDF Editor View")
            sharedViewModel.setTextMode(false)
            annotationEditor.exitEditMode()
        }
    }


    // 새로운 텍스트 상자 추가
    fun addNewTextBox(pageIndex: Int) {
        Log.d("PdfPageFragment", "${annotationEditor.getCurrentAnnotationInfo()}")
        val editText = annotationEditor.addTextBox(binding.pdfEditorView)
        annotationEditor.enterEditMode()
        // EditText의 위치와 크기 정보를 기반으로 Annotation 객체 생성
        val annotation = com.iguana.domain.model.Annotation(
            id = 0,
            content = editText.text.toString(),
            x = editText.x,
            y = editText.y,
            width = editText.width.toFloat(),
            height = editText.height.toFloat(),
            pageNumber = pageIndex
        )

        viewLifecycleOwner.lifecycleScope.launch {
            // 주석을 저장하도록 ViewModel 호출
            val generatedId = pdfPageViewModel.saveAnnotation(sharedViewModel.documentId, annotation, pageIndex)
            editText.tag = generatedId // EditText에 생성된 주석의 고유 ID 설정
        }
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

    override fun onTextEditingFinished(editText: EditText) {
        Log.d("AnnotationEditor", "Text editing finished")
        val pageIndex = arguments?.getInt(ARG_PAGE_INDEX, 0) ?: 0
        val annotationId = editText.tag as? Long ?: return

        val annotation = com.iguana.domain.model.Annotation(
            id = annotationId,
            content = editText.text.toString(),
            x = editText.x,
            y = editText.y,
            width = editText.width.toFloat(),
            height = editText.height.toFloat(),
            pageNumber = pageIndex
        )
        pdfPageViewModel.updateAnnotation(sharedViewModel.documentId, annotation)
    }

}
