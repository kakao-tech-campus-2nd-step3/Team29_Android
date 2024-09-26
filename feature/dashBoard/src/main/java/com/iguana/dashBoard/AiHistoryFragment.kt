package com.iguana.dashBoard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.iguana.dashBoard.databinding.FragmentAiHistoryBinding

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
        aiHistoryAdapter = AiHistoryAdapter(emptyList())
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