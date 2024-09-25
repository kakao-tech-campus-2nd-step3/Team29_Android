package com.iguana.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.iguana.ui.databinding.FragmentSideTabLayoutBinding

class SideTabLayoutFragment : Fragment() {
    private var _binding: FragmentSideTabLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSideTabLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun onDashboardClick() {
        // 나중에 DashboardFragment를 추가할 때 구현
    }

    fun onDocumentsClick() {
        // 나중에 DocumentsFragment를 추가할 때 구현
    }

    fun onFavoritesClick() {
        // 나중에 FavoritesFragment를 추가할 때 구현
    }

    fun onProfileClick() {
        // 나중에 ProfileFragment를 추가할 때 구현
    }

    fun onSettingsClick() {
        // 나중에 SettingsFragment를 추가할 때 구현
    }
}