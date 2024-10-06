package com.iguana.documents

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iguana.documents.databinding.ItemDocumentsFolderBinding
import com.iguana.documents.databinding.ItemDocumentsPdfBinding

class DocumentsAdapter(private val onItemClick: (DocumentItem) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<DocumentItem>()

    companion object {
        private const val VIEW_TYPE_FOLDER = 0
        private const val VIEW_TYPE_PDF = 1
    }

    fun setItems(newItems: List<DocumentItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_FOLDER -> FolderViewHolder(
                ItemDocumentsFolderBinding.inflate(inflater, parent, false)
            )
            VIEW_TYPE_PDF -> PdfViewHolder(
                ItemDocumentsPdfBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is FolderViewHolder -> holder.bind(item as DocumentItem.FolderItem)
            is PdfViewHolder -> holder.bind(item as DocumentItem.PdfItem)
        }
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is DocumentItem.FolderItem -> VIEW_TYPE_FOLDER
            is DocumentItem.PdfItem -> VIEW_TYPE_PDF
        }
    }

    class FolderViewHolder(private val binding: ItemDocumentsFolderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DocumentItem.FolderItem) {
            binding.fileName.text = item.name
            binding.fileTimestamp.text = "${item.fileCount} files"
            binding.bookmarkIcon.setImageResource(
                if (item.isBookmarked) com.iguana.designsystem.R.drawable.ic_file_saved_active
                else com.iguana.designsystem.R.drawable.ic_file_saved_inactive
            )
        }
    }

    class PdfViewHolder(private val binding: ItemDocumentsPdfBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DocumentItem.PdfItem) {
            binding.fileName.text = item.title
            binding.fileTimestamp.text = item.timestamp
            binding.bookmarkIcon.setImageResource(
                if (item.isBookmarked) com.iguana.designsystem.R.drawable.ic_file_saved_active
                else com.iguana.designsystem.R.drawable.ic_file_saved_inactive
            )
        }
    }
}

sealed class DocumentItem {
    data class FolderItem(
        val id : Long,
        val name: String,
        val fileCount: Int,
        val isBookmarked: Boolean
    ) : DocumentItem()

    data class PdfItem(
        val id : Long,
        val title: String,
        val timestamp: String,
        val isBookmarked: Boolean
    ) : DocumentItem()
}