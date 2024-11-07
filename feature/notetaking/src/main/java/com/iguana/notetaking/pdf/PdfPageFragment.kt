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
        _binding = FragmentPdfPageBinding.inflate(inflater, container, false)
        annotationEditor =
            AnnotationEditor(requireContext(), this, sharedViewModel.pageNumber.value ?: 0)
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
            sharedViewModel.setTextMode(false)
            annotationEditor.exitEditMode()
        }
    }


    // 새로운 텍스트 상자 추가
    fun addNewTextBox(pageIndex: Int) {
        val editText = annotationEditor.addTextBox(binding.pdfEditorView)
        editText.post {
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
                val generatedId = pdfPageViewModel.saveAnnotation(
                    sharedViewModel.documentId,
                    annotation,
                    pageIndex
                )
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
        val pageIndex = arguments?.getInt(ARG_PAGE_INDEX, 0) ?: 0
        val editText = binding.pdfEditorView.findViewWithTag<EditText>(annotation.id)

        // annotationId 확인
        val annotationId = editText?.tag as? Long
        if (annotationId == null) {
            Log.e("AnnotationEditor", "Annotation ID is null")
            return
        }

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


        viewLifecycleOwner.lifecycleScope.launch {
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

    fun deleteSelectedTextBox() {
        // 현재 선택된 텍스트 박스가 있는지 확인
        val annotationId = annotationEditor.currentEditText?.tag as? Long
        if (annotationId != null) {
            deleteAnnotation(annotationId)
            annotationEditor.exitEditMode()
        } else {
            Log.e("PdfPageFragment", "Error: Invalid annotation ID or no selected text box")
        }
    }

    // 주석 삭제
    private fun deleteAnnotation(annotationId: Long) {
        // 주석 ID로 태그가 지정된 EditText를 찾아서 UI에서 제거
        val editText = binding.pdfEditorView.findViewWithTag<EditText>(annotationId)
        editText?.let {
            binding.pdfEditorView.removeView(it)
            Log.d("PdfPageFragment", "Annotation with ID $annotationId removed from view")
        } ?: Log.e("PdfPageFragment", "Error: EditText with annotation ID $annotationId not found")

        // 주석 ID로 ViewModel에 삭제 요청
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                pdfPageViewModel.deleteAnnotation(sharedViewModel.documentId, annotationId)
                Log.d("PdfPageFragment", "Annotation $annotationId deleted from ViewModel")
            } catch (e: Exception) {
                Log.e("PdfPageFragment", "Failed to delete annotation: ${e.message}")
            }
        }
    }
}

