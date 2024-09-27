package com.iguana.notetaking

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class SideBarFragment : Fragment() {

    companion object {
        fun newInstance() = SideBarFragment()
    }

    private val viewModel: SideBarViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_side_bar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = view.findViewById<ViewPager2>(R.id.sideBarViewPager)
        val tabLayout = view.findViewById<TabLayout>(R.id.sideBarTabLayout)

        viewPager.adapter = SidebarAdapter(this)

        // 탭 레이아웃과 뷰페이저 연결
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "녹음"
                1 -> "AI"
                else -> throw IllegalArgumentException("Invalid position")
            }
        }.attach()
    }
}