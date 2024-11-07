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
import com.iguana.notetaking.pdf.model.AnnotationUIModel
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
        annotationEditor = AnnotationEditor(requireContext(), this, sharedViewModel.pageNumber.value ?: 0)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pdfUriString = arguments?.getString(ARG_PDF_URI)
        val pageIndex = arguments?.getInt(ARG_PAGE_INDEX, 0) ?: 0

        // 주석 로드
        pdfPageViewModel.loadAnnotations(sharedViewModel.documentId, pageIndex)


        pdfPageViewModel.annotations.observe(viewLifecycleOwner) { annotations ->
            displayAnnotations(annotations.filter { it.pageNumber == pageIndex })
        }

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
        editText.post {
            annotationEditor.enterEditMode()
            Log.d("PdfPageFragment", "EditText 위치와 크기: ${annotationEditor.getCurrentAnnotationInfo()}")

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
                Log.d("PdfPageFragment", "Generated Annotation ID: $generatedId")
                editText.tag = generatedId // 이 부분에서 Null이 아닌지 확인
            }
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

    override fun onTextEditingFinished(annotation: com.iguana.domain.model.Annotation) {
        Log.d("AnnotationEditor", "업데이트 호출")

        val pageIndex = arguments?.getInt(ARG_PAGE_INDEX, 0) ?: 0
        val editText = binding.pdfEditorView.findViewWithTag<EditText>(annotation.id)
        Log.d("AnnotationEditor", "edit 텍스트를 가져옴 id=${annotation.id} x=${annotation.x}, y=${annotation.y}, width=${annotation.width}, height=${annotation.height}")

        // annotationId 확인
        val annotationId = editText?.tag as? Long
        if (annotationId == null) {
            Log.e("AnnotationEditor", "Annotation ID is null")
            return
        }
        Log.d("AnnotationEditor", "주석 ID: $annotationId")

        // 새로운 Annotation 객체 생성하여 ViewModel에 업데이트
        val updatedAnnotation = com.iguana.domain.model.Annotation(
            id = annotationId,
            pageNumber = pageIndex,
            content = annotation.content,
            x = annotation.x,
            y = annotation.y,
            width = annotation.width,
            height = annotation.height
        )
        Log.d("AnnotationEditor", "업데이트 주석: x= ${updatedAnnotation.id}${updatedAnnotation.x}, y=${updatedAnnotation.y}, width=${updatedAnnotation.width}, height=${updatedAnnotation.height}")

        viewLifecycleOwner.lifecycleScope.launch {
            Log.d("AnnotationEditor", "(Fragment) Updating annotation: x=${updatedAnnotation.x}, y=${updatedAnnotation.y}, width=${updatedAnnotation.width}, height=${updatedAnnotation.height}")
            pdfPageViewModel.updateAnnotation(sharedViewModel.documentId, updatedAnnotation)
        }

    }

    private fun displayAnnotations(annotations: List<com.iguana.domain.model.Annotation>) {
        annotations.forEach { annotation ->
            // 동일한 주석 ID로 태그가 지정된 EditText가 이미 존재하는지 확인
            val existingEditText = binding.pdfEditorView.findViewWithTag<EditText>(annotation.id)

            // 동일 ID의 EditText가 없을 때만 새로 추가
            if (existingEditText == null) {
                val editText = annotationEditor.addTextBox(binding.pdfEditorView)
                editText.setText(annotation.content)

                // 주석 위치 설정
                editText.x = annotation.x
                editText.y = annotation.y

                // 주석 크기 설정
                editText.layoutParams.width = annotation.width.toInt()
                editText.layoutParams.height = annotation.height.toInt()

                // 주석 ID를 태그로 설정하여 중복 방지
                editText.tag = annotation.id
            }
        }
    }

}
