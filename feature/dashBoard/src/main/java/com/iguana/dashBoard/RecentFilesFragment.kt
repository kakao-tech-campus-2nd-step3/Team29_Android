package com.iguana.dashBoard

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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

    private lateinit var openPdfLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRecentFilesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecentFilesRecyclerView()
        observeRecentFiles()
        setupFilePicker()

        binding.plusIcon.setOnClickListener {
            openPdfLauncher.launch(arrayOf("application/pdf"))
        }
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

    private fun setupFilePicker() {
        openPdfLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {
                viewModel.handlePdf(it, requireContext())
            } ?: run {
                Toast.makeText(requireContext(), "PDF 선택 취소됨", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
