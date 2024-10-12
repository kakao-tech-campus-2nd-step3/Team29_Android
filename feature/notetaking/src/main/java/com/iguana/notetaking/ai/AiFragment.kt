package com.iguana.notetaking.ai

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.iguana.notetaking.databinding.FragmentAiBinding
import androidx.fragment.app.viewModels
import com.iguana.notetaking.NotetakingActivity


class AiFragment : Fragment() {

    companion object {
        fun newInstance() = AiFragment()
    }

    private var _binding: FragmentAiBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AiViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAiBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // 현재 페이지가 Fragment가 생성될 때 설정되도록 함
        val currentPage = (activity as? NotetakingActivity)?.getCurrentPage() ?: 1
        updateContentForPage(currentPage)

        super.onViewCreated(view, savedInstanceState)
        viewModel.pageNumber.observe(viewLifecycleOwner) { pageNumber ->
            binding.aiPageTextView.text = pageNumber?.toString()+" 페이지"
        }
    }

    // 페이지 번호 업데이트 메서드
    fun updateContentForPage(pageNumber: Int) {
        if (isAdded && !isDetached) { // Fragment가 활성 상태인지 확인
            viewModel.setPageNumber(pageNumber+1)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
