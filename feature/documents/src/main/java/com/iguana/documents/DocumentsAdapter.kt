package com.iguana.documents

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iguana.documents.databinding.ItemDocumentsFolderBinding
import com.iguana.documents.databinding.ItemDocumentsPdfBinding

class DocumentsAdapter(
    private val onItemClick: (DocumentItem) -> Unit,
    private val onItemLongClick: (DocumentItem, View) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<DocumentItem>()

    companion object {
        private const val VIEW_TYPE_FOLDER = 0
        private const val VIEW_TYPE_PDF = 1
    }

    fun setItems(newItems: List<DocumentItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
        Log.d("DocumentsAdapter", "Items updated: ${items.size} items")
        items.forEach { item ->
            Log.d("DocumentsAdapter", "Item: ${when(item) {
                is DocumentItem.FolderItem -> "Folder - ${item.name}"
                is DocumentItem.PdfItem -> "PDF - ${item.title}"
            }}")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_FOLDER -> {
                val binding = ItemDocumentsFolderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                FolderViewHolder(
                    binding = binding,
                    onItemClick = onItemClick,
                    onItemLongClick = onItemLongClick
                )
            }
            VIEW_TYPE_PDF -> {
                val binding = ItemDocumentsPdfBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                PdfViewHolder(
                    binding = binding,
                    onItemClick = onItemClick,
                    onItemLongClick = onItemLongClick
                )
            }
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is FolderViewHolder -> {
                if (item is DocumentItem.FolderItem) {
                    holder.bind(item)
                }
            }
            is PdfViewHolder -> {
                if (item is DocumentItem.PdfItem) {
                    holder.bind(item)
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is DocumentItem.FolderItem -> VIEW_TYPE_FOLDER
            is DocumentItem.PdfItem -> VIEW_TYPE_PDF
        }
    }

    class FolderViewHolder(
        private val binding: ItemDocumentsFolderBinding,
        private val onItemClick: (DocumentItem) -> Unit,
        private val onItemLongClick: (DocumentItem, View) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DocumentItem.FolderItem) {
            binding.fileName.text = item.name
            binding.bookmarkIcon.setImageResource(
                if (item.isBookmarked) com.iguana.designsystem.R.drawable.ic_file_saved_active
                else com.iguana.designsystem.R.drawable.ic_file_saved_inactive
            )
            binding.root.setOnClickListener { onItemClick(item) }
            binding.root.setOnLongClickListener { 
                onItemLongClick(item, binding.root)
                true
            }
        }
    }

    class PdfViewHolder(
        private val binding: ItemDocumentsPdfBinding,
        private val onItemClick: (DocumentItem) -> Unit,
        private val onItemLongClick: (DocumentItem, View) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DocumentItem.PdfItem) {
            binding.fileName.text = item.title
            binding.fileTimestamp.text = item.timestamp
            binding.bookmarkIcon.setImageResource(
                if (item.isBookmarked) com.iguana.designsystem.R.drawable.ic_file_saved_active
                else com.iguana.designsystem.R.drawable.ic_file_saved_inactive
            )
            binding.root.setOnClickListener { onItemClick(item) }
            binding.root.setOnLongClickListener { 
                onItemLongClick(item, binding.root)
                true
            }
        }
    }
}

sealed class DocumentItem {
    data class FolderItem(
        val id: Long,
        val name: String,
        val fileCount: Int,
        val isBookmarked: Boolean
    ) : DocumentItem()

    data class PdfItem(
        val id: Long,
        val title: String,
        val timestamp: String,
        val isBookmarked: Boolean,
        val url: String
    ) : DocumentItem()
}