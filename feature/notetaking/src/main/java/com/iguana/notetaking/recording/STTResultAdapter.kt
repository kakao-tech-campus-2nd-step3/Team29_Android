package com.iguana.notetaking.recording


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iguana.domain.model.record.SttItem
import com.iguana.notetaking.databinding.ItemSttBinding

class STTResultAdapter(
    private val sttItems: List<SttItem>
) : RecyclerView.Adapter<STTResultAdapter.STTViewHolder>() {

    inner class STTViewHolder(private val binding: ItemSttBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SttItem) {
            // 시간 정보 및 내용을 텍스트뷰에 설정
            binding.sttTimeStampTextView.text = "${item.startTime} ~ ${item.endTime}"
            binding.sttContentTextView.text = item.content
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): STTViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSttBinding.inflate(inflater, parent, false)
        return STTViewHolder(binding)
    }

    override fun onBindViewHolder(holder: STTViewHolder, position: Int) {
        holder.bind(sttItems[position])
    }

    override fun getItemCount(): Int = sttItems.size
}
