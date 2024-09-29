package com.iguana.notetaking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NotetakingViewModel : ViewModel() {
    private val _isSideBarVisible = MutableLiveData(true) // 초기값은 true로 설정
    val isSideBarVisible: LiveData<Boolean> get() = _isSideBarVisible

    // 사이드바의 가시성 상태를 토글하는 함수
    fun toggleSideBarVisibility() {
        _isSideBarVisible.value = _isSideBarVisible.value?.not()
    }
}
