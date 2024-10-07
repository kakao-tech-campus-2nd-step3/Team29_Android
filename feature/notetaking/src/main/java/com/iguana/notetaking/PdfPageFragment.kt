package com.iguana.notetaking

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.binish.photoeditorx.photoeditor.EnumClass
import com.binish.photoeditorx.photoeditor.PhotoEditor
import com.iguana.notetaking.databinding.FragmentPdfPageBinding
import dagger.hilt.android.AndroidEntryPoint
import com.binish.photoeditorx.photoeditor.OnPhotoEditorListener


@AndroidEntryPoint
class PdfPageFragment : Fragment() {

    private val viewModel: PdfViewerViewModel by viewModels()
    private var _binding: FragmentPdfPageBinding? = null
    private val binding get() = _binding!!
    // photoEditor 변수를 클래스 멤버 변수로 생성
    private lateinit var photoEditor: PhotoEditor

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pdfUriString = arguments?.getString(ARG_PDF_URI)
        val pageIndex = arguments?.getInt(ARG_PAGE_INDEX, 0) ?: 0

        Log.d("PdfPageFragment", "onViewCreated called") // 로그 추가

        val photoEditorView = binding.photoEditorView
        if (pdfUriString != null) {
            val pdfUri = Uri.parse(pdfUriString)
            val bitmap = viewModel.renderPage(pdfUri, pageIndex)
            // binding.pdfImageView.setImageBitmap(bitmap)
            photoEditorView.source?.setImageBitmap(bitmap) // 이미지 설정
        }
        // PhotoEditor 초기화
        photoEditor = PhotoEditor.Builder(requireContext(), photoEditorView)
            .setPinchTextScalable(true) // 텍스트 확대/축소 설정
            .build()

        // PhotoEditor의 리스너 설정
        photoEditor.setOnPhotoEditorListener(object : OnPhotoEditorListener {
            override fun onAddViewListener(
                view: View?,
                viewType: EnumClass.ViewType?,
                numberOfAddedViews: Int
            ) {
            }
            override fun onEditTextChangeListener(rootView: View?, text: String?, colorCode: Int) {
                // 텍스트 편집 시 호출되는 콜백
                // 텍스트 편집을 위한 대화상자 표시하지 않고 그 화면내에서 텍스트를 편집할 수 있니?
                showEditTextDialog(rootView, text, colorCode)
            }

            override fun onMoveViewChangeListener(
                view: View?,
                isInProgress: Boolean,
                rawX: Int,
                rawY: Int
            ) {
            }
            override fun onRemoveViewListener(viewType: EnumClass.ViewType?, numberOfAddedViews: Int) {}
            override fun onStartViewChangeListener(viewType: EnumClass.ViewType?) {}
            override fun onStopViewChangeListener(
                view: View?,
                viewType: EnumClass.ViewType?,
                currX: Float,
                currY: Float,
                rawX: Int,
                rawY: Int
            ) {
            }

            override fun onViewClicked(view: View?) {
            }
        })
    }

    // PhotoEditorX를 사용하여 텍스트 박스 추가
    fun addTextBox() {
        val inputText = "텍스트"
        val colorCode = Color.Blue.toArgb()
        photoEditor.addText(inputText, colorCode)
    }

    // 텍스트 편집을 위한 대화상자 표시 함수
    private fun showEditTextDialog(rootView: View?, text: String?, colorCode: Int) {
        val editText = EditText(requireContext())
        editText.setText(text)

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Text")
            .setView(editText)
            .setPositiveButton("OK") { dialog, _ ->
                val newText = editText.text.toString()
                if (rootView != null) {
                    photoEditor.editText(rootView, newText, colorCode) // 텍스트 수정
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }
//
//    fun addEditableTextBox() {
//        val editText = EditText(requireContext()).apply {
//            setText("텍스트")
//            setBackgroundColor(Color.Transparent.toArgb())
//            layoutParams = ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//            )
//
//            // 스타일 적용
//            setPadding(16, 16, 16, 16)
//            setTextColor(Color.Black.toArgb())
//            textSize = 16f
//
//            // 초기 위치를 중앙으로 설정
//            post {
//                x = (binding.pdfPageContainer.width / 2 - width / 2).toFloat()
//                y = (binding.pdfPageContainer.height / 2 - height / 2).toFloat()
//            }
//
//            // 터치 이벤트로 위치 이동 설정
//            var dX = 0f
//            var dY = 0f
//            var isDragging = false
//
//            setOnTouchListener { view, event ->
//                when (event.action) {
//                    MotionEvent.ACTION_DOWN -> {
//                        // 터치 시작 시점의 위치를 기록
//                        dX = view.x - event.rawX
//                        dY = view.y - event.rawY
//                        isDragging = false
//                    }
//                    MotionEvent.ACTION_MOVE -> {
//                        // 터치 움직임에 따라 뷰를 이동
//                        view.animate()
//                            .x(event.rawX + dX)
//                            .y(event.rawY + dY)
//                            .setDuration(0)
//                            .start()
//                        isDragging = true
//                    }
//                    MotionEvent.ACTION_UP -> {
//                        // 사용자가 드래그하지 않고 클릭만 한 경우 편집 모드로 전환
//                        if (!isDragging) {
//                            view.performClick() // 클릭 이벤트 수행
//                        }
//                    }
//                    else -> return@setOnTouchListener false
//                }
//                true
//            }
//
//            // 클릭 이벤트로 편집 모드 활성화
//            setOnClickListener {
//                this.isFocusableInTouchMode = true
//                this.requestFocus()
//            }
//        }
//        binding.pdfPageContainer.addView(editText)
//    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
