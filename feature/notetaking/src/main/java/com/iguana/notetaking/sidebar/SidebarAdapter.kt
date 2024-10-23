package com.iguana.notetaking.sidebar

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.iguana.notetaking.ai.AiFragment
import com.iguana.notetaking.recording.RecordFragment

class SidebarAdapter(fragment: Fragment, private val documentId: Long, private var currentPage: Int) : FragmentStateAdapter(fragment) {

        private val fragments = listOf<Fragment>(
            RecordFragment.newInstance(documentId, currentPage),
            AiFragment.newInstance(documentId, currentPage)
        )

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