package com.iguana.notetaking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.iguana.notetaking.NotetakingActivity.Companion.DEFAULT_TITLE
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotetakingViewModel @Inject constructor() : ViewModel() {

    var documentId: Long = -1L
    var pdfUri: String = ""
    var pdfTitle: String? = DEFAULT_TITLE


    private val _pageNumber = MutableLiveData<Int>()
    val pageNumber: LiveData<Int> get() = _pageNumber

    private val _isSideBarVisible = MutableLiveData(true) // 초기값은 true로 설정
    val isSideBarVisible: LiveData<Boolean> get() = _isSideBarVisible

    // record 버튼 활성화되어있는지
    private val _isRecordingActive = MutableLiveData(false)
    val isRecordingActive: LiveData<Boolean> get() = _isRecordingActive


    // AI 버튼 활성화되어있는지
    private val _isAIActive = MutableLiveData(false)
    val isAIActive: LiveData<Boolean> get() = _isAIActive

    // 이전 녹음 상태 추적 변수
    private var wasRecording = false

   private val _isTextMode =  MutableLiveData(false)
    val isTextMode: LiveData<Boolean> get() = _isTextMode

    // 텍스트 모드 토글 함수
    fun toggleTextMode() {
        _isTextMode.value = _isTextMode.value?.not()
    }
    // 페이지 번호 설정 함수
    fun setPageNumber(pageNumber: Int) {
        _pageNumber.value = pageNumber
    }

    // 사이드바의 가시성 상태를 토글하는 함수
    fun toggleSideBar() {
        _isSideBarVisible.value = _isSideBarVisible.value?.not()
    }

    // 녹음 시작 및 종료 상태 변경 함수
    fun toggleRecording() {
        wasRecording = _isRecordingActive.value == true
        _isRecordingActive.value = !_isRecordingActive.value!!
        showSideBar()
    }

    fun toggleAI() {
        _isAIActive.value = _isAIActive.value?.not()
        showSideBar()
    }

    // 녹음이 종료되었는지 확인하는 함수 (이전에 녹음 중 -> 녹음 종료)
    fun isRecordingStopped(): Boolean {
        return wasRecording && !_isRecordingActive.value!!
    }
    // 사이드바 보이기
    private fun showSideBar() {
        _isSideBarVisible.value = true
    }

    // 사이드바 숨기기
    fun hideSideBar() {
        _isSideBarVisible.value = false
    }
}
