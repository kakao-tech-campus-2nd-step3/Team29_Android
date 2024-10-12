package com.iguana.dashBoard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iguana.dashBoard.databinding.ItemRecentFilesBinding
import com.iguana.domain.model.RecentFile

class RecentFilesAdapter(
    private var recentFiles: List<RecentFile> = emptyList(),
    private val onFileClick: (RecentFile) -> Unit
) :
    RecyclerView.Adapter<RecentFilesAdapter.RecentFileViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentFileViewHolder {
        val binding =
            ItemRecentFilesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentFileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentFileViewHolder, position: Int) {
        holder.bind(recentFiles[position], onFileClick)
    }

    override fun getItemCount(): Int = recentFiles.size

    fun updateData(newRecentFiles: List<RecentFile>) {
        recentFiles = newRecentFiles
        notifyDataSetChanged()
    }

    class RecentFileViewHolder(private val binding: ItemRecentFilesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recentFile: RecentFile, onFileClick: (RecentFile) -> Unit) {
            binding.fileName.text = recentFile.fileName
            binding.fileTimestamp.text = formatLastOpened(recentFile.lastOpened)

            // 아이템 클릭 시 파일 열기
            binding.root.setOnClickListener {
                onFileClick(recentFile) // 클릭된 파일 전달
            }
        }

        private fun formatLastOpened(lastOpened: Long): String {
            return lastOpened.toString()
        }
    }
}
