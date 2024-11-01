package com.iguana.notetaking.pdf

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.iguana.notetaking.NotetakingViewModel
import com.iguana.notetaking.databinding.FragmentPdfPageBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PdfPageFragment : Fragment() {

    private val viewModel: PdfViewerViewModel by viewModels()
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
        pdfEditor = PdfEditor(requireContext()) // PdfEditor 초기화
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pdfUriString = arguments?.getString(ARG_PDF_URI)
        val pageIndex = arguments?.getInt(ARG_PAGE_INDEX, 0) ?: 0

        // 텍스트 모드일 때
        sharedViewModel.isTextMode.observe(viewLifecycleOwner, Observer { isEditMode ->

        })

        if (pdfUriString != null) {
            val pdfUri = Uri.parse(pdfUriString)
            val bitmap = viewModel.renderPage(pdfUri, pageIndex)

            // PhotoView에 이미지 설정 (확대/축소 기능)
            binding.photoView.setImageBitmap(bitmap)
        }

    }


    // 새로운 텍스트 상자 추가
    private fun addNewTextBox() {
        val editText = pdfEditor.addTextBox(binding.pdfEditorView) // pdfEditorView는 PDF 편집용 뷰 그룹
        pdfEditor.enableTextBoxEditing(editText) // 편집 모드 설정
    }

    // 텍스트 편집 완료 후 변경 사항 저장
    private fun saveTextChanges() {
        // 텍스트 상자의 위치, 내용 등을 저장하는 로직 구현
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
