package com.iguana.notetaking.pdf

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iguana.domain.usecase.ClearAnnotationsUseCase
import com.iguana.notetaking.util.PdfRendererHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PdfViewerViewModel @Inject constructor(
    private val pdfRendererHelper: PdfRendererHelper,
    private val clearAnnotationsUseCase: ClearAnnotationsUseCase
) :
    ViewModel() {

    private val _currentPageNumber = MutableLiveData<Int>() // 페이지 번호를 LiveData로 관리
    val currentPageNumber: LiveData<Int> get() = _currentPageNumber


    // PDF 파일의 특정 페이지를 렌더링
    suspend fun renderPage(uri: Uri, pageIdx: Int): Bitmap? {
        return withContext(Dispatchers.IO) {
            pdfRendererHelper.renderPage(uri, pageIdx)
        }
    }

    // 전체 PDF 페이지 수를 가져옴
    fun getPdfPageCount(uri: Uri, callback: (Int) -> Unit) {
        viewModelScope.launch {
            val pageCount = withContext(Dispatchers.IO) {
                pdfRendererHelper.getPageCount(uri)
            }
            callback(pageCount)
        }
    }

    // 현재 페이지 번호를 업데이트하는 메서드
    fun setCurrentPage(page: Int) {
        _currentPageNumber.value = page
    }

    // 주석 데이터 및 캐시된 PDF 파일 삭제
    fun clearCache() {
        Log.d("testt", "(ViewModel) clearCache 호출")
        try {
            // 메인 스레드에서 PDF 캐시를 삭제하기 위해 동기적으로 호출
            pdfRendererHelper.clearCache()
            viewModelScope.launch(Dispatchers.IO) {
                clearAnnotationsUseCase()
            }
        } catch (e: Exception) {
            Log.e("testt", "clearCache 호출 중 예외 발생: ${e.message}")
        }
    }

}