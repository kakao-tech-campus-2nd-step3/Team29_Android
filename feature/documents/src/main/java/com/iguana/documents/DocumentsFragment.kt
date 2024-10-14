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

@AndroidEntryPoint
class DocumentsFragment : Fragment() {

    private var _binding: FragmentDocumentsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: DocumentsAdapter
    private val viewModel: DocumentsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDocumentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupToolbar()
        observeViewModel()
        viewModel.loadAllDocuments()
    }

    private fun setupRecyclerView() {
        adapter = DocumentsAdapter(
            onItemClick = { item -> onItemClick(item) },
            onItemLongClick = { item -> showEditDeleteDialog(item) }
        )
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            this.adapter = this@DocumentsFragment.adapter
            addItemDecoration(GridSpacingItemDecoration(2, 16, true)) // 간격 추가
        }
    }

    private fun setupToolbar() {
        binding.btnBack.setOnClickListener { onBackPressed() }
        binding.btnEdit.setOnClickListener { /* 편집 기능 구현 */ }
        binding.btnAdd.setOnClickListener { showCreateFolderDialog() }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.documents.collect { folderContent ->
                folderContent?.let { updateUI(it) }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentFolderName.collect { folderName ->
                updateToolbarTitle(folderName)
            }
        }
    }

    private fun updateUI(folderContent: FolderContent) {
        val items = folderContent.content.map { item ->
            when (item.type) {
                "FOLDER" -> DocumentItem.FolderItem(
                    id = item.id,
                    name = item.name,
                    fileCount = item.totalElements,
                    isBookmarked = false
                )
                else -> DocumentItem.PdfItem(
                    id = item.id,
                    title = item.name,
                    timestamp = item.updatedAt,
                    isBookmarked = false
                )
            }
        }
        adapter.setItems(items)
    }

    private fun onItemClick(item: DocumentItem) {
        when (item) {
            is DocumentItem.FolderItem -> viewModel.loadSubItems(item.id, item.name)
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
            viewModel.createFolder(viewModel.currentFolderId, folderName)
        }
        dialog.show(parentFragmentManager, "CreateFolderDialog")
    }

    private fun showEditDeleteDialog(item: DocumentItem) {
        val options = arrayOf("수정", "삭제")
        AlertDialog.Builder(requireContext())
            .setTitle("옵션 선택")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditNameDialog(item)
                    1 -> showDeleteConfirmationDialog(item)
                }
            }
            .show()
    }

    private fun showDeleteConfirmationDialog(item: DocumentItem) {
        AlertDialog.Builder(requireContext())
            .setTitle("삭제 확인")
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
        val dialogView = layoutInflater.inflate(R.layout.dialog_create_folder, null)
        val editText = dialogView.findViewById<EditText>(R.id.editTextFolderName)
        val iconView = dialogView.findViewById<ImageView>(R.id.folderImg)

        editText.setText(when (item) {
            is DocumentItem.FolderItem -> item.name
            is DocumentItem.PdfItem -> item.title
        })

        // 아이템 타입에 따라 아이콘 설정
        iconView.setImageResource(when (item) {
            is DocumentItem.FolderItem -> com.iguana.designsystem.R.drawable.folder_item_background
            is DocumentItem.PdfItem -> com.iguana.designsystem.R.drawable.folder_item_background
        })

        AlertDialog.Builder(requireContext())
            .setTitle("이름 변경")
            .setView(dialogView)
            .setPositiveButton("확인") { _, _ ->
                val newName = editText.text.toString()
                val documentId = when (item) {
                    is DocumentItem.FolderItem -> item.id
                    is DocumentItem.PdfItem -> item.id
                }
                viewModel.updateDocumentName(documentId, newName)
            }
            .setNegativeButton("취소", null)
            .show()
    }

}

class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacing: Int,
    private val includeEdge: Boolean
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        outRect.left = column * spacing / spanCount
        outRect.right = spacing - (column + 1) * spacing / spanCount
        if (position >= spanCount) {
            outRect.top = spacing
        }
    }
}

