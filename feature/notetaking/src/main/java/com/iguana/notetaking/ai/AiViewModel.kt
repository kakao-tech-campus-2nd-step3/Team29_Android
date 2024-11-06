package com.iguana.notetaking.ai

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iguana.domain.model.ai.AIResult
import com.iguana.domain.model.ai.AIStatusResultByPage
import com.iguana.domain.model.ai.SummarizationStatus
import com.iguana.domain.usecase.GetAIResultByPageUseCase
import com.iguana.domain.usecase.GetAIStatusByPageUseCase
import com.iguana.domain.usecase.RequestAISummaryUseCase
import com.iguana.notetaking.recording.RecordFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AiViewModel @Inject constructor(
    private val getAIStatusByPageUseCase: GetAIStatusByPageUseCase,
    private val getAIResultByPageUseCase: GetAIResultByPageUseCase,
    private val requestAISummaryUseCase: RequestAISummaryUseCase,
    handle: SavedStateHandle
)  : ViewModel() {

    var documentId = handle.get<Long>(AiFragment.DOCUMENT_ID) ?: -1

    private val _pageNumber = MutableLiveData<Int>()
    val pageNumber: LiveData<Int> get() = _pageNumber

    private val _aiStatus = MutableLiveData<AIStatusResultByPage?>(AIStatusResultByPage(SummarizationStatus.NOT_REQUESTED))
    val aiStatus: LiveData<AIStatusResultByPage?> get() = _aiStatus

    private val _aiResult = MutableLiveData<AIResult?>()
    val aiResult: LiveData<AIResult?> get() = _aiResult


    fun setPageNumber(pageNumber: Int) {
        _pageNumber.value = pageNumber
        fetchAiStatus(pageNumber)
    }


    // AI 상태를 가져오는 메서드
    private fun fetchAiStatus(pageNumber: Int) {
        viewModelScope.launch {
            try {
                val status = getAIStatusByPageUseCase(documentId, pageNumber)
                _aiStatus.value = status
                // AI 상태가 완료된 경우에만 결과 가져오기
                if (status.isCompleted()) {
                    fetchAiResult(pageNumber)
                }
            } catch (e: Exception) {
                Log.d("testt", "AI 상태 가져올 때 에러 발생: $e")
                _aiStatus.value = null
            }
        }
    }

    // AI 결과를 가져오는 메서드
    private fun fetchAiResult(pageNumber: Int) {
        viewModelScope.launch {
            try {
                val result = getAIResultByPageUseCase(documentId, pageNumber)
                _aiResult.value = result
            } catch (e: Exception) {
                Log.d("testt", "AI 결과 가져올 때 에러 발생: $e")
                _aiResult.value = null
            }
        }
    }

    // AI 요청 메서드
    fun requestAI() {
        viewModelScope.launch {
            val pages = (0.._pageNumber.value!!).toList()
            requestAISummaryUseCase(documentId, pages)
            fetchAiStatus(pageNumber = pageNumber.value!!)
        }
    }
}