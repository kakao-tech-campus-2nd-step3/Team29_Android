package com.iguana.notetaking.recording

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel

class RecordViewModel: ViewModel() {
    private val _pageNumber = MutableLiveData<Int>()
    val pageNumber: LiveData<Int> get() = _pageNumber

    fun setPageNumber(pageNumber: Int) {
        _pageNumber.value = pageNumber
    }

}