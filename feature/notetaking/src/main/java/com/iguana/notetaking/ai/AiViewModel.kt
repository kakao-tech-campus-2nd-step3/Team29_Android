package com.iguana.notetaking.ai

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iguana.domain.model.ai.AIResult
import com.iguana.domain.model.ai.AIStatusResultByPage
import com.iguana.domain.usecase.GetAIResultByPageUseCase
import com.iguana.domain.usecase.GetAIStatusByPageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AiViewModel @Inject constructor(
    private val getAIStatusByPageUseCase: GetAIStatusByPageUseCase,
    private val getAIResultByPageUseCase: GetAIResultByPageUseCase
)  : ViewModel() {

    private val _pageNumber = MutableLiveData<Int>()
    val pageNumber: LiveData<Int> get() = _pageNumber

    private val _aiStatus = MutableLiveData<AIStatusResultByPage?>()
    val aiStatus: LiveData<AIStatusResultByPage?> get() = _aiStatus

    private val _aiResult = MutableLiveData<AIResult?>()
    val aiResult: LiveData<AIResult?> get() = _aiResult


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
                // AI 상태가 완료된 경우에만 결과 가져오기
                if (status.isCompleted()) {
                    fetchAiResult(pageNumber)
                }
            }.onFailure {
                Log.d("testt", "Error: $it")
                _aiStatus.value = null
            }
        }
    }

    // AI 결과를 가져오는 메서드
    private fun fetchAiResult(pageNumber: Int) {
        viewModelScope.launch {
            val result = getAIResultByPageUseCase(documentId, pageNumber)
            result.onSuccess { aiResult ->
                _aiResult.value = aiResult
            }.onFailure {
                Log.d("testt", "Error fetching AI result: $it")
                _aiResult.value = null
            }
        }
    }
}