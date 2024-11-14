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
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.iguana.domain.model.ai.AIResult
import com.iguana.domain.model.ai.AIStatusResultByPage
import com.iguana.domain.model.record.SttResult
import com.iguana.domain.model.record.SttStatusResultByPage
import com.iguana.notetaking.NotetakingViewModel
import com.iguana.notetaking.R
import com.iguana.notetaking.ai.AiFragment
import com.iguana.notetaking.databinding.FragmentRecordBinding
import com.iguana.notetaking.util.HtmlFormatter
import com.iguana.notetaking.util.hide
import com.iguana.notetaking.util.show
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

        // 녹음 상태 변화 관찰
        viewModel.recordingStatus.observe(viewLifecycleOwner) { recordingStatus ->
            updateUiForRecordingState(recordingStatus)
            if (recordingStatus == RecordingStatus.COMPLETED) {
                viewModel.fetchSttStatus() // 녹음 완료 시 STT 상태 요청
            }
        }

        // STT 상태 변화 관찰
        viewModel.sttStatus.observe(viewLifecycleOwner) { sttStatus ->
            if (sttStatus != null) {
                updateUiForSttStatus(sttStatus)
            }
            if (sttStatus?.isCompleted() == true) {
                viewModel.fetchSttResult(viewModel.pageNumber.value ?: 1)
            }
        }

        // STT 결과 변화 관찰
        viewModel.sttResult.observe(viewLifecycleOwner) { sttResult ->
            sttResult?.let { updateUiForResult(it) }
        }

        sharedViewModel.pageNumber.observe(viewLifecycleOwner) { pageNumber ->
            viewModel.setPageNumber(pageNumber)
        }

        binding.sttRefreshButton.setOnClickListener {
            Toast.makeText(requireContext(), "STT 상태를 새로고침합니다.", Toast.LENGTH_SHORT).show()
            Log.d("testt", "sttStausButton clicked")
            viewModel.fetchSttStatus()
        }
    }



    // Fragment 화면에 표시되며, 입력을 받을 수 있는 상태
    override fun onResume() {
        super.onResume()
        // 브로드캐스트 등록 시 커스텀 권한을 추가하여 외부 앱의 접근을 제한
        val filter = IntentFilter(BROADCAST_RECORDING_FINISHED)
        requireContext().registerReceiver(
            recordingReceiver,
            filter,
            "com.iguana.notetaking.PERMISSION_RECEIVE_BROADCAST", // 커스텀 권한 추가
            null
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

    private fun showSTTContent() {
        Log.d("RecordFragment", "showSTTContent() 호출됨")
        binding.sttStatus.hide()
        binding.recyclerView.show()
    }

    private fun showSTTStatus() {
        Log.d("RecordFragment", "showSTTStatus() 호출됨")
        binding.sttStatus.show()
        binding.sttStatusTextView.show()
        binding.sttRefreshButton.show()
        binding.recyclerView.hide()
    }

    private fun hideSTT() {
        Log.d("RecordFragment", "hideSTT() 호출됨")
        binding.recyclerView.hide()
        binding.sttStatus.hide()
    }


    // 녹음 상태에 따라 UI 업데이트 (녹음 완료 시에만 STT 상태 요청 표시되도록)
    private fun updateUiForRecordingState(recordingStatus: RecordingStatus) {
        Log.d("RecordFragment", "(UpdateUIForRecordingState) 녹음 상태: $recordingStatus")
        when (recordingStatus) {
            RecordingStatus.NOT_STARTED -> {
                Log.d("RecordFragment", "녹음 시작 안 한 상태됨")
                binding.recordStatusTextView.text = getString(R.string.is_not_recording)
                hideSTT()
            }
            RecordingStatus.RECORDING -> {
                Log.d("RecordFragment", "녹음 중 상태됨")
                binding.recordStatusTextView.text = getString(R.string.recording_in_progress)
                hideSTT()
            }
            RecordingStatus.COMPLETED -> {
                Log.d("RecordFragment", "녹음 완료 상태됨")
                binding.recordStatusTextView.text = getString(R.string.is_not_recording)
                showSTTStatus()
            }
        }
    }
    private fun updateUiForSttStatus(status: SttStatusResultByPage) {
        showSTTStatus()
        Log.d("RecordFragment", "(UpdateUIForSttStatus) STT 상태: $status")
        binding.sttStatusTextView.text = when {
            status.isInProgress() -> {
                binding.sttRefreshButton.show()
                getString(R.string.stt_in_progress)
            }
            status.isCompleted() -> {
                binding.sttRefreshButton.hide()
                getString(R.string.stt_completed)
            }
            status.isNotRequested() -> {
                binding.sttRefreshButton.hide()
                getString(R.string.stt_not_requested)
            }
            status.isFailed() -> {
                binding.sttRefreshButton.hide()
                getString(R.string.stt_failed)
            }
            else -> {
                binding.sttRefreshButton.hide()
                getString(R.string.status_unavailable)
            }
        }
    }

    private fun updateUiForResult(result: SttResult) {
        Log.d("RecordFragment", "(UpdateUIForResult) STT 결과: $result")
        // RecyclerView에 LayoutManager 설정
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = STTResultAdapter(result.sttContents)
        binding.recyclerView.adapter = adapter

        // RecyclerView 표시
        binding.recyclerView.visibility = View.VISIBLE
        binding.sttStatus.visibility = View.GONE
    }

}
