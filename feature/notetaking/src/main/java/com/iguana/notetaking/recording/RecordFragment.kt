package com.iguana.notetaking.recording

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import com.iguana.notetaking.NotetakingActivity
import com.iguana.notetaking.ai.AiFragment
import com.iguana.notetaking.databinding.FragmentRecordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecordFragment() : Fragment() {

    companion object {
        private const val DOCUMENT_ID = "documentId"
        private const val CURRENT_PAGE = "currentPage"
        fun newInstance(documentId: Long, currentPage: Int) = RecordFragment().apply {
            arguments = bundleOf(
                DOCUMENT_ID to documentId,
                CURRENT_PAGE to currentPage
            )
        }
    }

    private var _binding: FragmentRecordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RecordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecordBinding.inflate(inflater, container, false)

        arguments?.let {
            viewModel.documentId = it.getLong(AiFragment.DOCUMENT_ID)
            viewModel.setPageNumber(it.getInt(AiFragment.CURRENT_PAGE))
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
