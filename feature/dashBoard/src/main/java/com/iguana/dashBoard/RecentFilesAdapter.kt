package com.iguana.dashBoard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iguana.dashBoard.databinding.ItemRecentFilesBinding

class RecentFilesAdapter(private var recentFiles: List<RecentFile>) :
    RecyclerView.Adapter<RecentFilesAdapter.RecentFileViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentFileViewHolder {
        val binding = ItemRecentFilesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentFileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentFileViewHolder, position: Int) {
        holder.bind(recentFiles[position])
    }

    override fun getItemCount(): Int = recentFiles.size

    fun updateData(newRecentFiles: List<RecentFile>) {
        recentFiles = newRecentFiles
        notifyDataSetChanged()
    }

    class RecentFileViewHolder(private val binding: ItemRecentFilesBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recentFile: RecentFile) {
            binding.fileName.text = recentFile.title
            binding.fileTimestamp.text = recentFile.subtitle
        }
    }
}