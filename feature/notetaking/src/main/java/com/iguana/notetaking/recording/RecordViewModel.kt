package com.iguana.notetaking.recording

import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iguana.domain.usecase.SavePageTurnEventUseCase
import com.iguana.domain.usecase.UploadPageTurnEventsUseCase
import com.iguana.domain.usecase.UploadRecordingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val uploadRecordingUseCase: UploadRecordingUseCase,
    private val uploadPageTurnEventsUseCase: UploadPageTurnEventsUseCase,
    private val savePageTurnEventUseCase: SavePageTurnEventUseCase
) : ViewModel() {
    var documentId: Long = -1L
    private var recorder: MediaRecorder? = null


    private val _recordingStatus = MutableLiveData<Boolean>()
    val recordingStatus: LiveData<Boolean> get() = _recordingStatus

    private var startTimeMillis: Long = 0L

    private var prevPage: Int = 1 // 이전 페이지 번호 추적
    private val _pageNumber = MutableLiveData<Int>() // 현재 페이지 번호
    val pageNumber: LiveData<Int> get() = _pageNumber

    fun setPageNumber(pageNumber: Int) {
        _pageNumber.value = pageNumber
        if (isRecording()) {
            viewModelScope.launch {
                savePageTurnEvent(pageNumber)
            }
            prevPage = pageNumber
        } else {
            Log.d("RecordViewModel", "녹음 상태가 아님, 저장하지 않음")
        }
    }

    fun startRecording(context: Context) {
        try {
            val intent = Intent(context, RecordingService::class.java).apply {
                action = "START_RECORDING"
            }
            context.startService(intent)
            _recordingStatus.value = true // 녹음 시작 전에 상태를 true로 설정
            startTimeMillis = System.currentTimeMillis()
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

    private suspend fun savePageTurnEvent(currentPage: Int) {
        savePageTurnEventUseCase(documentId, prevPage, currentPage, startTimeMillis)
    }
    private fun isRecording(): Boolean {
        return recordingStatus.value ?: false
    }
}