package com.iguana.documents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.app.AlertDialog
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.iguana.documents.databinding.FragmentDocumentsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Stack
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Rect
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.iguana.domain.model.FolderContent
import kotlinx.coroutines.launch
import com.iguana.documents.R
import com.iguana.documents.DocumentsViewModel
import com.iguana.domain.model.FolderContentItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.iguana.notetaking.NotetakingActivity
import kotlinx.coroutines.flow.StateFlow
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.iguana.documents.databinding.DialogAddOptionsBinding
import android.app.Dialog
import android.widget.Button
import android.view.WindowManager
import com.iguana.documents.databinding.DialogEditNameBinding
import android.os.Build
import android.view.Window
import androidx.activity.OnBackPressedCallback

@AndroidEntryPoint
class DocumentsFragment : Fragment() {

    private var _binding: FragmentDocumentsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: DocumentsAdapter
    private val viewModel: DocumentsViewModel by viewModels()

    private lateinit var openPdfLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDocumentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupToolbar()
        observeViewModel()
        setupFilePicker()
        viewModel.loadAllDocuments()
    }

    private fun setupRecyclerView() {
        adapter = DocumentsAdapter(
            onItemClick = { item ->
                when (item) {
                    is DocumentItem.FolderItem -> {
                        viewModel.loadFolderContents(item.id, item.name)
                    }
                    is DocumentItem.PdfItem -> {
                        val intent = Intent(requireContext(), NotetakingActivity::class.java).apply {
                            putExtra("PDF_URI", item.url)
                            putExtra("PDF_TITLE", item.title)
                            putExtra("DOCUMENT_ID", item.id)
                        }
                        Log.d("DocumentsFragment", "Document ID: ${item.id}, PDF URI: ${item.url}, PDF Title: ${item.title}")
                        startActivity(intent)
                    }
                }
            },
            onItemLongClick = { item, view -> showEditDeleteDialog(item, view) }
        )
        binding.recyclerView.adapter = adapter
    }

    private fun setupToolbar() {
        binding.btnBack.setOnClickListener { onBackPressed() }
        binding.btnAdd.setOnClickListener { showAddOptionsDialog() }
    }

    private fun setupFilePicker() {
        openPdfLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {
                viewModel.uploadPdf(it, requireContext())
            } ?: run {
                Toast.makeText(requireContext(), "PDF 선택 취소됨", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAddOptionsDialog() {
        val rootView = requireActivity().window.decorView.findViewById<ViewGroup>(android.R.id.content)
        val dimView = View(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#E6000000"))
            alpha = 0f
            translationX = rootView.width.toFloat()
            translationY = -rootView.height.toFloat()
        }

        rootView.addView(dimView)

        dimView.animate()
            .alpha(1f)
            .translationX(0f)
            .translationY(0f)
            .setDuration(300)
            .start()

        val dialogBinding = DialogAddOptionsBinding.inflate(layoutInflater)

        val dialog = PopupWindow(
            dialogBinding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            elevation = 10f

            setOnDismissListener {
                dimView.animate()
                    .alpha(0f)
                    .translationX(rootView.width.toFloat())
                    .translationY(-rootView.height.toFloat())
                    .setDuration(300)
                    .withEndAction {
                        rootView.removeView(dimView)
                    }
                    .start()
            }
        }

        dialogBinding.uploadFileLayout.setOnClickListener {
            dialog.dismiss()
            openPdfLauncher.launch(arrayOf("application/pdf"))
        }

        dialogBinding.createFolderLayout.setOnClickListener {
            dialog.dismiss()
            showCreateFolderDialog()
        }

        dialog.showAsDropDown(binding.btnAdd)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.documentItems.collect { items ->
                adapter.setItems(items)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentFolderName.collect { folderName ->
                updateToolbarTitle(folderName)
            }
        }
    }

//    private fun updateUI(folderContent: List<FolderContentItem>) {
//        val items = folderContent.map { item ->
//            when (item.type.uppercase()) {
//                "FOLDER" -> DocumentItem.FolderItem(
//                    id = item.id,
//                    name = item.name,
//                    fileCount = item.totalElements,
//                    isBookmarked = false
//                )
//                "DOCUMENT" -> DocumentItem.PdfItem(
//                    id = item.id,
//                    title = item.name,
//                    timestamp = item.updatedAt,
//                    isBookmarked = false,
//                )
//                else -> DocumentItem.PdfItem(
//                    id = item.id,
//                    title = item.name,
//                    timestamp = item.updatedAt,
//                    isBookmarked = false,
//                )
//            }
//        }
//        adapter.setItems(items)
//    }

    private fun onItemClick(item: DocumentItem) {
        when (item) {
            is DocumentItem.FolderItem -> viewModel.loadFolderContents(item.id, item.name)
            is DocumentItem.PdfItem -> openPdf(item.id, item.title)
        }
    }

    private fun openPdf(id: Long, title: String) {
        // PDF 열기 로직
    }

    private fun onBackPressed() {
        viewModel.navigateUp()
    }

    private fun updateToolbarTitle(title: String) {
        binding.tvTitle.text = title
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun showCreateFolderDialog() {
        val dialog = CreateFolderDialogFragment { folderName ->
            viewModel.createFolder(folderName)
        }
        dialog.show(parentFragmentManager, "CreateFolderDialog")
    }

    private fun showEditDeleteDialog(item: DocumentItem, anchorView: View) {
        val rootView = requireActivity().window.decorView.findViewById<ViewGroup>(android.R.id.content)
        val dimView = View(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#80000000"))
            alpha = 0f
        }
        
        rootView.addView(dimView)
        
        dimView.animate()
            .alpha(1f)
            .setDuration(200)
            .start()

        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_options, null)
        val dialog = PopupWindow(
            dialogView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            elevation = 10f
            
            setOnDismissListener {
                dimView.animate()
                    .alpha(0f)
                    .setDuration(200)
                    .withEndAction {
                        rootView.removeView(dimView)
                    }
                    .start()
            }
        }

        // 옵션 클릭 리스너 설정
        dialogView.findViewById<LinearLayout>(R.id.editNameLayout).setOnClickListener {
            dialog.dismiss()
            showEditNameDialog(item)
        }
        
        dialogView.findViewById<LinearLayout>(R.id.deleteLayout).setOnClickListener {
            dialog.dismiss()
            showDeleteConfirmationDialog(item)
        }

        // 선택된 아이템 위치 기준으로 팝업 표시
        val location = IntArray(2)
        anchorView.getLocationInWindow(location)
        dialog.showAtLocation(
            anchorView,
            android.view.Gravity.NO_GRAVITY,
            location[0],
            location[1] + (anchorView.height / 2)
        )
    }

    private fun showDeleteConfirmationDialog(item: DocumentItem) {
        AlertDialog.Builder(requireContext())
            .setTitle("")
            .setMessage("정말로 이 ${if (item is DocumentItem.FolderItem) "폴더" else "파일"}를 삭제하시겠습니까?")
            .setPositiveButton("삭제") { _, _ ->
                when (item) {
                    is DocumentItem.FolderItem -> {
                        viewModel.deleteFolder(item.id)
                        Log.d("DocumentsFragment", "폴더 삭제 요청: ${item.id}")
                    }
                    is DocumentItem.PdfItem -> {
                        viewModel.deleteFile(item.id)
                        Log.d("DocumentsFragment", "파일 삭제 요청: ${item.id}")
                    }
                }
            }
            .setNegativeButton("취소") { _, _ ->
                Log.d("DocumentsFragment", "삭제 취소됨")
            }
            .show()
    }

    private fun showEditNameDialog(item: DocumentItem) {
        val dialog = Dialog(requireContext())
        val dialogBinding = DialogEditNameBinding.inflate(layoutInflater)
        
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogBinding.root)
        
        // 다이얼로그를 전체 화면으로 설정
        dialog.window?.let { window ->
            window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            
            // 플래그 설정
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            
            // 배경 설정
            window.setBackgroundDrawable(ColorDrawable(Color.parseColor("#CC000000")))
            
            // 상태바 설정
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or 
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            )
            
            // 상태바 투명하게
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.statusBarColor = Color.TRANSPARENT
            }
        }

        dialogBinding.editTextFolderName.setText(when (item) {
            is DocumentItem.FolderItem -> item.name
            is DocumentItem.PdfItem -> item.title
        })

        dialogBinding.folderImg.setImageResource(when (item) {
            is DocumentItem.FolderItem -> com.iguana.designsystem.R.drawable.ic_folder_large
            is DocumentItem.PdfItem -> com.iguana.designsystem.R.drawable.ic_file_large
        })

        dialogBinding.btnConfirm.setOnClickListener {
            val newName = dialogBinding.editTextFolderName.text.toString()
            when (item) {
                is DocumentItem.FolderItem -> {
                    viewModel.updateFolderName(item.id, newName)
                    Log.d("DocumentsFragment", "폴더 이름 변경 요청: ${item.id}, 새 이름: $newName")
                }
                is DocumentItem.PdfItem -> {
                    viewModel.updateDocumentName(viewModel.currentFolderId, item.id, newName)
                    Log.d("DocumentsFragment", "문서 이름 변경 요청: ${item.id}, 새 이름: $newName")
                }
            }
            dialog.dismiss()
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshCurrentFolder()
    }

//    private fun mapToDocumentItems(folderContent: FolderContent): List<DocumentItem> {
//        return folderContent.map { item ->
//            when (item.type.uppercase()) {
//                "FOLDER" -> DocumentItem.FolderItem(
//                    id = item.id,
//                    name = item.name,
//                    fileCount = item.totalElements,
//                    isBookmarked = false
//                )
//                "DOCUMENT" -> DocumentItem.PdfItem(
//                    id = item.id,
//                    title = item.name,
//                    timestamp = item.updatedAt,
//                    isBookmarked = false
//                )
//                else -> DocumentItem.PdfItem(
//                    id = item.id,
//                    title = item.name,
//                    timestamp = item.updatedAt,
//                    isBookmarked = false
//                )
//            }
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 뒤로가기 콜백 등록 (true는 콜백을 즉시 활성화한다는 의미)
        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Toast.makeText(requireContext(), "뒤로가기가 비활성화 되어있습니다", Toast.LENGTH_SHORT).show()
            }
        })
    }

}

