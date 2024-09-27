package com.iguana.notetaking

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PdfPageFragment : Fragment() {

    private val viewModel: PdfViewerViewModel by viewModels()

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
        return inflater.inflate(R.layout.fragment_pdf_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pdfUriString = arguments?.getString(ARG_PDF_URI)
        val pageIndex = arguments?.getInt(ARG_PAGE_INDEX, 0) ?: 0

        Log.d("PdfPageFragment", "onViewCreated called") // 로그 추가

        val imageView = view.findViewById<ImageView>(R.id.pdfImageView)
        if (pdfUriString != null) {
            val pdfUri = Uri.parse(pdfUriString)
            val bitmap = viewModel.renderPage(pdfUri, pageIndex)
            imageView.setImageBitmap(bitmap)
        }
    }
}
