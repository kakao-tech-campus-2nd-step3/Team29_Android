package com.iguana.notetaking.recording

import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.iguana.domain.usecase.UploadPageTurnEventsUseCase
import com.iguana.domain.usecase.UploadRecordingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val uploadRecordingUseCase: UploadRecordingUseCase,
    private val uploadPageTurnEventsUseCase: UploadPageTurnEventsUseCase
) : ViewModel() {
    var documentId: Long = -1L
    private var recorder: MediaRecorder? = null
    private var isRecording = false // 녹음 상태 확인 변수

    private val _recordingStatus = MutableLiveData<Boolean>()
    val recordingStatus: LiveData<Boolean> get() = _recordingStatus

    private val _pageNumber = MutableLiveData<Int>()
    val pageNumber: LiveData<Int> get() = _pageNumber

    fun setPageNumber(pageNumber: Int) {
        _pageNumber.value = pageNumber
    }

    fun startRecording(context: Context) {
        try {
            val intent = Intent(context, RecordingService::class.java).apply {
                action = "START_RECORDING"
            }
            context.startService(intent)
            _recordingStatus.value = true
        } catch (e: Exception) {
            Log.e("RecordViewModel", "녹음 시작 실패: ${e.message}")
        }
    }

    fun stopRecording(context: Context) {
        val intent = Intent(context, RecordingService::class.java).apply {
            action = "STOP_RECORDING"
        }
        context.startService(intent)
        _recordingStatus.value = false
    }
}