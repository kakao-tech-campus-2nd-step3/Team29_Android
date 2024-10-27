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
        set(value) = run { field = value }
    var pdfUri: String = ""
        set(value) = run { field = value }

    var pdfTitle: String? = "무제"
        set(value) = run { field = value }


    private val _pageNumber = MutableLiveData<Int>()
    val pageNumber: LiveData<Int> get() = _pageNumber

    private val _isSideBarVisible = MutableLiveData(true) // 초기값은 true로 설정
    val isSideBarVisible: LiveData<Boolean> get() = _isSideBarVisible

    // record 버튼 활성화되어있는지
    private val _isRecordActive = MutableLiveData(false)
    val isRecordActive: LiveData<Boolean> get() = _isRecordActive


    // AI 버튼 활성화되어있는지
    private val _isAIActive = MutableLiveData(false)
    val isAIActive: LiveData<Boolean> get() = _isAIActive

    // 이전 녹음 상태 추적 변수
    private var wasRecording = false

    fun setPageNumber(pageNumber: Int) {
        _pageNumber.value = pageNumber
    }

    // 사이드바의 가시성 상태를 토글하는 함수
    fun toggleSideBarVisibility() {
        _isSideBarVisible.value = _isSideBarVisible.value?.not()
    }

    // 사이드바 보이게 하는 함수
    fun showSideBar() {
        _isSideBarVisible.value = true
    }
    // 사이드바 숨기기
    fun hideSideBar() {
        _isSideBarVisible.value = false
    }


    // 액티브 상태를 토글
    fun toggleRecordTabActive() {
        _isRecordActive.value = _isRecordActive.value?.not()
        // 상태가 변경될 때 이전 녹음 상태를 저장
        wasRecording = _isRecordActive.value == true
        showSideBar()
    }
    fun toggleAITabActive() {
        _isAIActive.value = _isAIActive.value?.not()
        showSideBar()
    }

    // 녹음이 종료되었는지 확인하는 함수 (이전에 녹음 중 -> 녹음 종료)
    fun isRecordingStopped(): Boolean {
        return wasRecording && !_isRecordActive.value!!
    }
}
