package com.iguana.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.iguana.ui.databinding.FragmentSideTabLayoutBinding

class SideTabLayoutFragment : Fragment() {

    private var _binding: FragmentSideTabLayoutBinding? = null
    private val binding get() = _binding!!

    private val _selectedItem = MutableLiveData<Int>(0)
    val selectedItem: LiveData<Int> = _selectedItem

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSideTabLayoutBinding.inflate(inflater, container, false)
        binding.fragment = this
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.dashboard.setOnClickListener { onDashboardClick() }
        binding.documents.setOnClickListener { onDocumentsClick() }
        binding.favorites.setOnClickListener { onFavoritesClick() }
        binding.profile.setOnClickListener { onProfileClick() }
        binding.settings.setOnClickListener { onSettingsClick() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun onDashboardClick() {
        _selectedItem.value = 0
        (activity as? BaseActivity)?.showDashBoard()
    }

    fun onDocumentsClick() {
        _selectedItem.value = 1
        (activity as? BaseActivity)?.showDocuments()
    }

    fun onFavoritesClick() {
        _selectedItem.value = 2
    }

    fun onProfileClick() {
        _selectedItem.value = 3
    }

    fun onSettingsClick() {
        _selectedItem.value = 4
    }
}