package com.iguana.notetaking.recording

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.iguana.notetaking.NotetakingViewModel
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
    private val sharedViewModel: NotetakingViewModel by activityViewModels()

    private val recordingReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val filePath = intent?.getStringExtra("filePath")
            val fileName = intent?.getStringExtra("fileName")
            if (filePath != null && fileName != null) {
                viewModel.setFileInfo(filePath, fileName) // 파일 정보를 ViewModel에 설정
                viewModel.processRecordingAndEvents()
            } else {
                Log.e("RecordFragment", "filePath 또는 fileName이 null입니다.")
            }
        }
    }

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
        observeRecordingState()

        sharedViewModel.pageNumber.observe(viewLifecycleOwner) { pageNumber ->
            viewModel.setPageNumber(pageNumber)
        }
    }

    // Fragment 화면에 표시되며, 입력을 받을 수 있는 상태
    override fun onResume() {
        super.onResume()
        // 브로드캐스트 등록
        requireContext().registerReceiver(
            recordingReceiver,
            IntentFilter(BROADCAST_RECORDING_FINISHED)
        )
    }

    // 사용자와의 상호작용 멈출 때 호출
    override fun onPause() {
        super.onPause()
        // BroadcastReceiver 해제
        requireContext().unregisterReceiver(recordingReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun startRecording(context: Context) {
        viewModel.startRecording(context)
    }

    private fun stopRecording(context: Context) {
        viewModel.stopRecording(context)
    }

    private fun observeRecordingState() {
        sharedViewModel.isRecordingActive.observe(viewLifecycleOwner) { isActive ->
            if (isActive) startRecording(requireContext()) else stopRecording(requireContext())
        }
    }
}
