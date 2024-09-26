package com.iguana.dashBoard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.iguana.dashBoard.databinding.FragmentRecentFilesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecentFilesFragment : Fragment() {
    private var _binding: FragmentRecentFilesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RecentFilesViewModel by viewModels()
    private lateinit var recentFilesAdapter: RecentFilesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRecentFilesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecentFilesRecyclerView()
        observeRecentFiles()
    }

    private fun setupRecentFilesRecyclerView() {
        recentFilesAdapter = RecentFilesAdapter(emptyList())
        binding.recentFilesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = recentFilesAdapter
        }
    }

    private fun observeRecentFiles() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.recentFiles.collect { recentFiles ->
                    recentFilesAdapter.updateData(recentFiles)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
