package com.iguana.documents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.iguana.documents.databinding.FragmentDocumentsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Stack
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Rect
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.iguana.domain.model.FolderContent
import kotlinx.coroutines.launch

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
        adapter = DocumentsAdapter(::onItemClick)
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            this.adapter = this@DocumentsFragment.adapter
        }
    }

    private fun setupToolbar() {
        binding.btnBack.setOnClickListener { onBackPressed() }
        binding.btnEdit.setOnClickListener { /* 편집 기능 구현 */ }
        binding.btnAdd.setOnClickListener { /* 추가 기능 구현 */ }
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
                    fileCount = item.totalElements.toInt(),
                    isBookmarked = false  // 이 정보가 FolderContentItem에 없다면 기본값으로 설정
                )
                else -> DocumentItem.PdfItem(
                    id = item.id,
                    title = item.name,
                    timestamp = item.updatedAt,
                    isBookmarked = false  // 이 정보가 FolderContentItem에 없다면 기본값으로 설정
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
}

class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacing: Int,
    private val includeEdge: Boolean
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        outRect.left = column * spacing / spanCount
        outRect.right = spacing - (column + 1) * spacing / spanCount
        if (position >= spanCount) {
            outRect.top = spacing
        }
    }
}