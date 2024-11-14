package com.iguana.notetaking.recording


import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iguana.domain.model.record.SttResult
import com.iguana.domain.model.record.SttStatus
import com.iguana.domain.model.record.SttStatusResultByPage
import com.iguana.domain.usecase.DeletePageTurnEventsUseCase
import com.iguana.domain.usecase.DeleteRecordingUseCase
import com.iguana.domain.usecase.GetSTTResultByPageUseCase
import com.iguana.domain.usecase.GetSTTStatusByPageUseCase
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
    private val savePageTurnEventUseCase: SavePageTurnEventUseCase,
    private val deletePageTurnEventsUseCase: DeletePageTurnEventsUseCase,
    private val deleteRecordingUseCase: DeleteRecordingUseCase,
    private val getSTTStatusByPageUseCase: GetSTTStatusByPageUseCase,
    private val getSTTResultByPageUseCase: GetSTTResultByPageUseCase
) : ViewModel() {
    var documentId: Long = -1L

    private val _recordingStatus = MutableLiveData<RecordingStatus>(RecordingStatus.NOT_STARTED)
    val recordingStatus: LiveData<RecordingStatus> get() = _recordingStatus

    private var startTimeMillis: Long = 0L

    private var prevPage: Int = 1 // 이전 페이지 번호 추적
    private val _pageNumber = MutableLiveData<Int>() // 현재 페이지 번호
    val pageNumber: LiveData<Int> get() = _pageNumber
    private var filePath: String? = null
    private var fileName: String? = null

    private val _sttStatus = MutableLiveData<SttStatusResultByPage?>(SttStatusResultByPage(SttStatus.NOT_REQUESTED))
    val sttStatus: LiveData<SttStatusResultByPage?> get() = _sttStatus

    private val _sttResult = MutableLiveData<SttResult?>()
    val sttResult: LiveData<SttResult?> get() = _sttResult

    init {
        _recordingStatus.value = RecordingStatus.NOT_STARTED
        fetchSttStatus(pageNumber=0)
    }


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
        fetchSttStatus()
    }

    // stt 상태 가져오는 메서드 (Fragment에서 호출)
    fun fetchSttStatus() {
        fetchSttStatus(pageNumber = pageNumber.value!!)
        Log.d("RecordViewModel", "fetchSttStatus: ${pageNumber.value} ${_sttStatus.value}")
    }

    // STT 상태를 가져오는 메서드
    private fun fetchSttStatus(pageNumber: Int) {
        viewModelScope.launch {
            try {
                val status = getSTTStatusByPageUseCase(documentId, pageNumber)
                _sttStatus.value = status
                // STT 상태가 완료된 경우에만 결과 가져오기
                if (status.isCompleted()) {
                    fetchSttResult(pageNumber)
                }
            } catch (e: Exception) {
                Log.d("RecordViewModel", "STT 상태 가져올 때 에러 발생: $e")
                _sttStatus.value = null
            }
        }
    }

    // STT 결과를 가져오는 메서드
    fun fetchSttResult(pageNumber: Int) {
        viewModelScope.launch {
            try {
                val result = getSTTResultByPageUseCase(documentId, pageNumber)
                _sttResult.value = result
            } catch (e: Exception) {
                Log.d("RecordViewModel", "STT 결과 가져올 때 에러 발생: $e")
                _sttResult.value = null
            }
        }
    }


    fun startRecording(context: Context) {
        try {
            val intent = Intent(context, RecordingService::class.java).apply {
                action = ACTION_START_RECORDING
            }
            context.startService(intent)
            _recordingStatus.value = RecordingStatus.RECORDING
            startTimeMillis = System.currentTimeMillis()
        } catch (e: Exception) {
            Log.e("RecordViewModel", "녹음 시작 실패: ${e.message}")
        }
    }

    fun stopRecording(context: Context) {
        sendStopIntent(context)
        _recordingStatus.value = RecordingStatus.COMPLETED
    }

    private fun sendStopIntent(context: Context) {
        val intent = Intent(context, RecordingService::class.java).apply {
            action = ACTION_STOP_RECORDING
        }
        context.startService(intent)
    }

    fun processRecordingAndEvents() {
        viewModelScope.launch {
            try {
                // filePath 또는 fileName이 null일 경우
                if (filePath == null || fileName == null) {
                    Log.e("RecordViewModel", "filePath 또는 fileName이 null입니다. 업로드를 중단합니다.")
                    return@launch
                }

                // 1. 녹음 파일 업로드
                val recordingId = uploadRecordingUseCase(documentId, filePath ?: return@launch, "$fileName")
                // 2. 페이지 이동 이벤트 업로드
                uploadPageTurnEventsUseCase(documentId, recordingId)
                // 3. 로컬에 저장된 페이지 이동 이벤트 파일 삭제
                deletePageTurnEventsUseCase(documentId)
                // 4. 로컬에 저장된 녹음 파일 삭제
                deleteRecordingUseCase(filePath!!)
            } catch (e: Exception) {
                Log.e("RecordViewModel", "업로드 중 오류 발생: ${e.message}")
            }
        }
    }

    private suspend fun savePageTurnEvent(currentPage: Int) {
        savePageTurnEventUseCase(documentId, prevPage, currentPage, startTimeMillis)
    }

    private fun isRecording(): Boolean {
        return recordingStatus.value == RecordingStatus.RECORDING
    }

    // 파일 정보 설정
    fun setFileInfo(path: String, name: String) {
        filePath = path
        fileName = name
    }
}