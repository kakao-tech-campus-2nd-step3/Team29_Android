package com.iguana.notetaking.ai

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.iguana.notetaking.databinding.FragmentAiBinding
import androidx.fragment.app.viewModels
import com.iguana.domain.model.ai.AIStatusResultByPage
import com.iguana.notetaking.NotetakingActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AiFragment(private val documentId: Long) : Fragment() {

    companion object {
        fun newInstance(documentId: Long) = AiFragment(documentId)
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
        Log.d("testt", "AiFragment onViewCreated")
        viewModel.setDocumentId(documentId)
        // 현재 페이지가 Fragment가 생성될 때 설정되도록 함
        val currentPage = (activity as? NotetakingActivity)?.getCurrentPage() ?: 1
        updateContentForPage(currentPage)

        super.onViewCreated(view, savedInstanceState)
        viewModel.pageNumber.observe(viewLifecycleOwner) { pageNumber ->
            binding.aiPageTextView.text = pageNumber?.toString()+" 페이지"
        }
        // AI 상태가 변경될 때 상태에 따른 UI 업데이트
        viewModel.aiStatus.observe(viewLifecycleOwner) { aiStatus ->
            aiStatus?.let { status ->
                updateUiForStatus(status)
            } ?: run {
                binding.aiStatusTextView.text = "상태를 가져올 수 없습니다."
            }
        }
    }

    // 페이지 번호 업데이트 메서드
    fun updateContentForPage(pageNumber: Int) {
        if (isAdded && !isDetached) { // Fragment가 활성 상태인지 확인
            viewModel.setPageNumber(pageNumber+1)
        }
    }

    // AI 상태에 따라 UI 업데이트
    private fun updateUiForStatus(status: AIStatusResultByPage) {
        // 상태 메서드를 활용하여 UI를 업데이트
        when {
            status.isInProgress() -> {
                binding.aiStatusTextView.text = "AI 요약이 진행 중입니다."
            }
            status.isCompleted() -> {
                binding.aiStatusTextView.text = "AI 요약이 완료되었습니다."
            }
            status.isNotRequested() -> {
                binding.aiStatusTextView.text = "AI 요약이 요청되지 않았습니다."
            }
            status.isFailed() -> {
                binding.aiStatusTextView.text = "AI 요약이 실패했습니다."
            }
            else -> {
                binding.aiStatusTextView.text = "상태를 확인할 수 없습니다."
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
