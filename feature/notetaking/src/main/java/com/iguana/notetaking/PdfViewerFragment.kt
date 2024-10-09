package com.iguana.notetaking

import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.iguana.notetaking.databinding.FragmentPdfViewerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PdfViewerFragment : Fragment() {

    private val viewModel: PdfViewerViewModel by viewModels()
    private var _binding: FragmentPdfViewerBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_PDF_URI = "PDF_URI"

        fun newInstance(pdfUri: Uri): PdfViewerFragment {
            val fragment = PdfViewerFragment()
            val args = Bundle()
            args.putString(ARG_PDF_URI, pdfUri.toString())
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPdfViewerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pdfUriString = arguments?.getString(ARG_PDF_URI)

        if (pdfUriString != null) {
            val pdfUri = Uri.parse(pdfUriString)
            // 전체 PDF 페이지 수를 가져와 어댑터에 설정
            viewModel.getPdfPageCount(pdfUri) { pageCount ->
                binding.pdfViewPager.adapter = PdfPageAdapter(this, pdfUri, pageCount)
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
