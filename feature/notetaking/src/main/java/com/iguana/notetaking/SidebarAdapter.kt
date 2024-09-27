package com.iguana.notetaking

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class SidebarAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    // 보여줄 탭의 개수: 녹음, AI
    override fun getItemCount(): Int {
        return 2
    }

    // 각 탭에 해당하는 프래그먼트 반환
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RecordFragment.newInstance()
            1 -> AiFragment.newInstance()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}