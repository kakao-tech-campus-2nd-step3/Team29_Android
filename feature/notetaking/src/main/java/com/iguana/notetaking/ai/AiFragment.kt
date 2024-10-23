package com.iguana.notetaking.ai

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.iguana.notetaking.databinding.FragmentAiBinding
import androidx.fragment.app.viewModels
import com.iguana.domain.model.ai.AIResult
import com.iguana.domain.model.ai.AIStatusResultByPage
import com.iguana.notetaking.NotetakingActivity
import com.iguana.notetaking.R
import com.iguana.notetaking.recording.RecordFragment
import com.iguana.notetaking.util.hide
import com.iguana.notetaking.util.isVisible
import com.iguana.notetaking.util.show
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AiFragment : Fragment() {

    companion object {
        const val DOCUMENT_ID = "documentId"
        const val CURRENT_PAGE = "currentPage"
        fun newInstance(documentId: Long, currentPage: Int) = AiFragment().apply {
            arguments = Bundle().apply {
                putLong(DOCUMENT_ID, documentId)
                putInt(CURRENT_PAGE, currentPage)
            }
        }
    }

    private var _binding: FragmentAiBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AiViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments?.let {
            viewModel.documentId = it.getLong(DOCUMENT_ID)
            viewModel.setPageNumber(it.getInt(CURRENT_PAGE))
        }

        _binding = FragmentAiBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // AI 상태가 변경될 때 상태에 따른 UI 업데이트
        viewModel.aiStatus.observe(viewLifecycleOwner) { aiStatus ->
            aiStatus?.let { status ->
                updateUiForStatus(status)
            } ?: run {
                binding.aiStatusTextView.text = getString(R.string.status_unavailable)
                binding.aiStatusTextView.show()
                binding.aiContentTextView.hide()
            }
        }
        // AI 결과가 있으면 해당 결과를 반영
        viewModel.aiResult.observe(viewLifecycleOwner) { aiResult ->
            aiResult?.let { result ->
                updateUiForResult(result)
            }
        }
    }

    // 페이지 번호 업데이트 메서드
    fun updateContentForPage(pageNumber: Int) {
        if (isAdded && !isDetached) { // Fragment가 활성 상태인지 확인
            viewModel.setPageNumber(pageNumber + 1)
        }
    }

    // AI 상태에 따라 UI 업데이트
    private fun updateUiForStatus(status: AIStatusResultByPage) {
        binding.aiStatusTextView.text = when {
            status.isInProgress() -> getString(R.string.ai_in_progress)
            status.isCompleted() -> getString(R.string.ai_completed)
            status.isNotRequested() -> getString(R.string.ai_not_requested)
            status.isFailed() -> getString(R.string.ai_failed)
            else -> getString(R.string.status_unavailable)
        }
    }

    // AI 결과에 따라 UI 업데이트
    private fun updateUiForResult(result: AIResult) {
        // 요약 텍스트 업데이트
        binding.aiContentTextView.text = result.formattedSummary.takeIf { result.hasSummary }
            ?: getString(R.string.no_summary_available)

        // 문제 텍스트 업데이트
        binding.aiProblemTextView.text = result.formattedProblem.takeIf { result.hasProblem }
            ?: getString(R.string.no_summary_available)

        showAIContent()
    }

    private fun showAIContent() {
        binding.aiStatusTextView.hide()
        binding.problemTitleTextView.show()
        binding.aiContentTextView.show()
        binding.aiProblemTextView.show()
        binding.summaryTitleTextView.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
