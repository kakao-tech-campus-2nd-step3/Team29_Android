package com.iguana.dashBoard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.iguana.dashBoard.databinding.FragmentRecentFilesBinding

class RecentFilesFragment : Fragment() {
    private var _binding: FragmentRecentFilesBinding? = null
    private val binding get() = _binding!!

    private lateinit var recentFilesAdapter: RecentFilesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRecentFilesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        recentFilesAdapter = RecentFilesAdapter(emptyList())
        binding.recentFilesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = recentFilesAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

data class RecentFile(val title: String, val subtitle: String)