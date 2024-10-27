package com.iguana.notetaking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotetakingViewModel @Inject constructor(
    handle: SavedStateHandle
) : ViewModel() {
    private val _isSideBarVisible = MutableLiveData(true) // 초기값은 true로 설정
    val isSideBarVisible: LiveData<Boolean> get() = _isSideBarVisible

    var documentId: Long = -1L
    private val _pageNumber = MutableLiveData<Int>()
    val pageNumber: LiveData<Int> get() = _pageNumber

    fun setPageNumber(pageNumber: Int) {
        _pageNumber.value = pageNumber
    }

    // 사이드바의 가시성 상태를 토글하는 함수
    fun toggleSideBarVisibility() {
        _isSideBarVisible.value = _isSideBarVisible.value?.not()
    }
}
