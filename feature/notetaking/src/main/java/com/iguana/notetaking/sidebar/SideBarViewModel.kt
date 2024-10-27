package com.iguana.notetaking.sidebar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SideBarViewModel @Inject constructor(
    handle: SavedStateHandle
) : ViewModel() {
    var documentId: Long = -1L
    private val _pageNumber = MutableLiveData<Int>().apply { value = 0 }
    val pageNumber: LiveData<Int> get() = _pageNumber

    fun setPageNumber(pageNumber: Int) {
        _pageNumber.value = pageNumber
    }
}