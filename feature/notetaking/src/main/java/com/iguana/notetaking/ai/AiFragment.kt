package com.iguana.notetaking.ai

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.iguana.notetaking.databinding.FragmentAiBinding
import androidx.fragment.app.viewModels
import com.iguana.domain.model.ai.AIResult
import com.iguana.domain.model.ai.AIStatusResultByPage
import com.iguana.notetaking.NotetakingViewModel
import com.iguana.notetaking.R
import com.iguana.notetaking.recording.RecordFragment
import com.iguana.notetaking.util.HtmlFormatter
import com.iguana.notetaking.util.hide
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

    // Shared ViewModel (Activity 범위)
    private val sharedViewModel: NotetakingViewModel by activityViewModels()

    // Local ViewModel (Fragment 범위)
    private val aiViewModel: AiViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments?.let {
            aiViewModel.documentId = it.getLong(DOCUMENT_ID)
            aiViewModel.setPageNumber(it.getInt(CURRENT_PAGE))
        }

        _binding = FragmentAiBinding.inflate(inflater, container, false)
        binding.viewModel = aiViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // AI 상태가 변경될 때 상태에 따른 UI 업데이트
        aiViewModel.aiStatus.observe(viewLifecycleOwner) { aiStatus ->
            aiStatus?.let { status ->
                updateUiForStatus(status)
                // AI 상태가 완료된 경우에만 aiResult를 observe하도록 설정
                if (status.isCompleted()) {
                    aiViewModel.aiResult.observe(viewLifecycleOwner) { aiResult ->
                        aiResult?.let { result ->
                            updateUiForResult(result)
                        }
                    }
                }
            } ?: run {
                binding.aiStatusTextView.text = getString(R.string.status_unavailable)
                binding.aiStatusTextView.show()
                binding.aiContentTextView.hide()
            }
        }
        sharedViewModel.pageNumber.observe(viewLifecycleOwner) { pageNumber ->
            aiViewModel.setPageNumber(pageNumber)
        }
        binding.aiButton.setOnClickListener {
            Toast.makeText(requireContext(), "AI 요청이 완료되었습니다. AI 요청은 30초-1분 정도 소요될 수 있습니다.", Toast.LENGTH_SHORT).show()
            aiViewModel.requestAI()
        }
        binding.refreshButton.setOnClickListener {
            Toast.makeText(requireContext(), "AI 상태를 새로고침합니다.", Toast.LENGTH_SHORT).show()
            Log.d("testt", "aiStatusButton clicked")
            aiViewModel.fetchAiStatus()
        }
    }

    // AI 상태에 따라 UI 업데이트
    private fun updateUiForStatus(status: AIStatusResultByPage) {
        hideAIContent()
        binding.aiStatusTextView.text = when {
            status.isInProgress() -> {
                binding.refreshButton.show()
                getString(R.string.ai_in_progress)
            }
            status.isCompleted() -> {
                binding.aiStatus.hide()
                getString(R.string.ai_completed)
            }
            status.isNotRequested() -> {
                binding.refreshButton.hide()
                getString(R.string.ai_not_requested)
            }
            status.isFailed() -> {
                binding.refreshButton.hide()
                getString(R.string.ai_failed)
            }
            else -> {
                binding.refreshButton.hide()
                getString(R.string.status_unavailable)
            }
        }
    }

    // AI 결과에 따라 UI 업데이트
    private fun updateUiForResult(result: AIResult) {
        binding.aiContentTextView.text = result.summary?.let { HtmlFormatter.formatAsHtml(it) }
            ?: getString(R.string.no_summary_available)

        binding.aiProblemTextView.text = result.problem?.let { HtmlFormatter.formatAsHtml(it) }
            ?: getString(R.string.no_problem_available)
        showAIContent ()
    }

    private fun showAIContent() {
        binding.aiStatusTextView.hide()
        binding.problemTitleTextView.show()
        binding.aiContentTextView.show()
        binding.aiProblemTextView.show()
        binding.summaryTitleTextView.show()
        binding.divider.show()
    }

    private fun hideAIContent() {
        binding.aiStatusTextView.show()
        binding.problemTitleTextView.hide()
        binding.aiContentTextView.hide()
        binding.aiProblemTextView.hide()
        binding.summaryTitleTextView.hide()
        binding.divider.hide()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
