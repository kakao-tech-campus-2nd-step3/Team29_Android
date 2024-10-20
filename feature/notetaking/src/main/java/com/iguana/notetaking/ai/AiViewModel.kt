package com.iguana.notetaking.ai

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iguana.domain.model.ai.AIStatusResultByPage
import com.iguana.domain.usecase.GetAIStatusByPageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AiViewModel @Inject constructor(
    private val getAIStatusByPageUseCase: GetAIStatusByPageUseCase
)  : ViewModel() {

    private val _pageNumber = MutableLiveData<Int>()
    val pageNumber: LiveData<Int> get() = _pageNumber

    private val _aiStatus = MutableLiveData<AIStatusResultByPage?>()
    val aiStatus: LiveData<AIStatusResultByPage?> get() = _aiStatus


    // documentId 변수
    private var documentId: Long = -1

    fun setPageNumber(pageNumber: Int) {
        _pageNumber.value = pageNumber
        fetchAiStatus(pageNumber)
        Log.d("testt", "fetchAiStatus: $pageNumber")
    }
    // documentId 설정 메서드
    fun setDocumentId(id: Long) {
        documentId = id
    }


    // AI 상태를 가져오는 메서드
    private fun fetchAiStatus(pageNumber: Int) {
        viewModelScope.launch {
            val result = getAIStatusByPageUseCase(documentId, pageNumber)
            result.onSuccess { status ->
                _aiStatus.value = status
            }.onFailure {
                Log.d("testt", "Error: $it")
                _aiStatus.value = null
            }
        }
    }



}