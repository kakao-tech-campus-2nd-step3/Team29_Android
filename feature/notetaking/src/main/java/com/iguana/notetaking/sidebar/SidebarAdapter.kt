package com.iguana.notetaking.sidebar

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.iguana.notetaking.ai.AiFragment
import com.iguana.notetaking.recording.RecordFragment

class SidebarAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragments: MutableList<Fragment> = mutableListOf()

    init {
        // 초기화할 때 프래그먼트를 추가
        fragments.add(RecordFragment.newInstance())
        fragments.add(AiFragment.newInstance())
    }

    // 보여줄 탭의 개수: 녹음, AI
    override fun getItemCount(): Int {
        return fragments.size
    }

    // 각 탭에 해당하는 프래그먼트 반환
    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    // 특정 포지션의 프래그먼트를 반환하는 메서드
    fun getFragment(position: Int): Fragment {
        return fragments[position]
    }
}