package com.iguana.dashBoard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.iguana.dashBoard.databinding.FragmentAiHistoryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AiHistoryFragment : Fragment() {
    private var _binding: FragmentAiHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var aiHistoryAdapter: AiHistoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAiHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        // 더미 데이터 생성
        val dummyData = listOf(
            AiHistoryItem("2024.11.01", "노트 요약 1"),
            AiHistoryItem("2024.11.02", "노트 요약 2"),
            AiHistoryItem("2024.11.03", "노트 요약 3"),
            AiHistoryItem("2024.11.04", "노트 요약 4"),
            AiHistoryItem("2024.11.05", "노트 요약 5"),
            AiHistoryItem("2024.11.05", "노트 요약 6")
        )

        aiHistoryAdapter = AiHistoryAdapter(dummyData)
        binding.aiHistoryRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = aiHistoryAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

data class AiHistoryItem(val date: String, val title: String)
