package com.iguana.notetaking
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iguana.notetaking.util.PdfRendererHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PdfViewerViewModel @Inject constructor(private val pdfRendererHelper: PdfRendererHelper) :
    ViewModel() {

    // PDF 파일의 특정 페이지를 렌더링
    fun renderPage(uri: Uri, pageIdx: Int): Bitmap? {
        return pdfRendererHelper.renderPage(uri, pageIdx)
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

}