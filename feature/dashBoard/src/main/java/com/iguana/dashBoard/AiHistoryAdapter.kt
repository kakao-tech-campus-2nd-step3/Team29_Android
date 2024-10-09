package com.iguana.dashBoard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iguana.dashBoard.databinding.ItemAiHistoryBinding
class AiHistoryAdapter(private val aiHistoryItems: List<AiHistoryItem>) :
    RecyclerView.Adapter<AiHistoryAdapter.AiHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AiHistoryViewHolder {
        val binding = ItemAiHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AiHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AiHistoryViewHolder, position: Int) {
        holder.bind(aiHistoryItems[position])
    }

    override fun getItemCount(): Int = aiHistoryItems.size

    class AiHistoryViewHolder(private val binding: ItemAiHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(aiHistoryItem: AiHistoryItem) {
            binding.dateTextView.text = aiHistoryItem.date
            binding.titleTextView.text = aiHistoryItem.title
        }
    }
}