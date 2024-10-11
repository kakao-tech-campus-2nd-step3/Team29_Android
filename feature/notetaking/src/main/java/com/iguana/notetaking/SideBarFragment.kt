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
import com.iguana.notetaking.databinding.FragmentSideBarBinding

class SideBarFragment : Fragment() {

    companion object {
        fun newInstance() = SideBarFragment()
    }

    private val viewModel: SideBarViewModel by viewModels()
    private var _binding: FragmentSideBarBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSideBarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = binding.sideBarViewPager
        val tabLayout = binding.sideBarTabLayout

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

    // 사이드바 내용 업데이트 메서드
    fun updatePageContent(pageNumber: Int) {
        // 어댑터를 통해 각 탭의 프래그먼트 접근
        val adapter = binding.sideBarViewPager.adapter as SidebarAdapter
        val recordFragment = adapter.getFragment(0) as? RecordFragment
        val aiFragment = adapter.getFragment(1) as? AiFragment

        // 각 프래그먼트에 페이지 번호 업데이트
        recordFragment?.updateContentForPage(pageNumber)
        aiFragment?.updateContentForPage(pageNumber)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}