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

@AndroidEntryPoint
class DocumentsFragment : Fragment() {

    private var _binding: FragmentDocumentsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: DocumentsAdapter
    private val currentItems = mutableListOf<DocumentItem>()
    private val folderStack = Stack<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDocumentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupToolbar()
        loadRootFolder()
    }

    private fun setupRecyclerView() {
        val spacing = 1
        adapter = DocumentsAdapter(::onItemClick)
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            addItemDecoration(GridSpacingItemDecoration(2, spacing, false))
            adapter = this@DocumentsFragment.adapter
            setPadding(0, 0, 0, 0)
            clipToPadding = true
        }
    }

    private fun setupToolbar() {
        binding.btnBack.setOnClickListener { onBackPressed() }
        binding.btnEdit.setOnClickListener { /* 편집 기능 구현 */ }
        binding.btnAdd.setOnClickListener { /* 추가 기능 구현 */ }
    }

    private fun loadRootFolder() {
        currentItems.clear()
        currentItems.addAll(listOf(
            DocumentItem.FolderItem("NOTAI 가이드", 5, false)))
        adapter.setItems(currentItems)
        updateToolbarTitle("문서")
    }

    private fun onItemClick(item: DocumentItem) {
        when (item) {
            is DocumentItem.FolderItem -> openFolder(item.name)
            is DocumentItem.PdfItem -> openPdf(item.title)
        }
    }

    private fun openFolder(folderName: String) {
        folderStack.push(folderName)
        // 해당 폴더의 아이템들을 로드하는 로직
        currentItems.clear()
        currentItems.addAll(listOf(
            DocumentItem.PdfItem("$folderName - NOTAI 가이드 - 1", "2023-05-03 11:45", true),
            DocumentItem.PdfItem("$folderName - NOTAI 템플릿 - 2", "2023-05-04 16:20", true)
        ))
        adapter.setItems(currentItems)
        updateToolbarTitle(folderName)
    }

    private fun openPdf(pdfTitle: String) {
        // PDF 열기 로직
    }

    private fun onBackPressed() {
        if (folderStack.isNotEmpty()) {
            folderStack.pop()
            if (folderStack.isEmpty()) {
                loadRootFolder()
            } else {
                openFolder(folderStack.peek())
            }
        } else {
            // Fragment 종료 또는 이전 화면으로 이동
        }
    }

    private fun updateToolbarTitle(title: String) {
        binding.tvTitle.text = title
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