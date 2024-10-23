package com.iguana.notetaking.sidebar

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.google.android.material.tabs.TabLayoutMediator
import com.iguana.notetaking.ai.AiFragment
import com.iguana.notetaking.databinding.FragmentSideBarBinding
import com.iguana.notetaking.recording.RecordFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SideBarFragment() : Fragment() {

    companion object {
        const val DOCUMENT_ID = "documentId"
        const val CURRENT_PAGE = "currentPage"
        fun newInstance(documentId: Long, currentPage: Int) = SideBarFragment().apply {
            arguments = bundleOf(
                DOCUMENT_ID to documentId,
                CURRENT_PAGE to currentPage
            )
        }
    }

    private val viewModel: SideBarViewModel by viewModels()
    private var _binding: FragmentSideBarBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSideBarBinding.inflate(inflater, container, false)

        arguments?.let {
            viewModel.documentId = it.getLong(DOCUMENT_ID)
            viewModel.setPageNumber(it.getInt(CURRENT_PAGE))
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = binding.sideBarViewPager
        val tabLayout = binding.sideBarTabLayout

        viewPager.adapter = SidebarAdapter(this, viewModel.documentId, viewModel.pageNumber.value ?: 0)

        // 탭 레이아웃과 뷰페이저 연결
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "녹음"
                1 -> "AI"
                else -> throw IllegalArgumentException("Invalid position")
            }
        }.attach()

        // 페이지 번호 변경 시 호출되는 메서드
        viewModel.pageNumber.observe(viewLifecycleOwner) { pageNumber ->
            (viewPager.adapter as SidebarAdapter).getFragment(0)?.let {
                (it as RecordFragment).updateContentForPage(pageNumber)
            }
            (viewPager.adapter as SidebarAdapter).getFragment(1)?.let {
                (it as AiFragment).updateContentForPage(pageNumber)
            }
        }
    }

    // 사이드바 내용 업데이트 메서드
    fun updatePageNumber(pageNumber: Int) {
        viewModel.setPageNumber(pageNumber)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}