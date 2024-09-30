package com.iguana.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iguana.data.local.entity.RecentFileEntity
import com.iguana.domain.model.RecentFile
import com.iguana.domain.repository.RecentFileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashBoardViewModel @Inject constructor(
    private val recentFileRepository: RecentFileRepository
) : ViewModel() {

    private val _recentFiles = MutableStateFlow<List<RecentFile>>(emptyList())
    val recentFiles: StateFlow<List<RecentFile>> = _recentFiles.asStateFlow()

    init {
        loadRecentFiles()
    }

    private fun loadRecentFiles() {
        viewModelScope.launch {
            recentFileRepository.getRecentFiles().collect { files ->
                _recentFiles.value = files
            }
        }
    }

    // 파일을 열 때 호출되는 함수
    fun openFile(id: String, fileName: String, fileUri: String) {
        viewModelScope.launch {
            recentFileRepository.insertRecentFile(id, fileName, fileUri)
        }
    }

    // 북마크 업데이트 함수
    fun updateBookmark(fileId: String, bookmarkedPage: Int) {
        viewModelScope.launch {
            recentFileRepository.updateBookmark(fileId, bookmarkedPage)
        }
    }

    // 오래된 파일 정리 함수
    fun cleanupOldFiles(daysToKeep: Int = 30) {
        viewModelScope.launch {
            recentFileRepository.cleanupOldFiles(daysToKeep)
        }
    }

}
