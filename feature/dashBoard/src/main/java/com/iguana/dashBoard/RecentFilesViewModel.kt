package com.iguana.dashBoard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iguana.data.local.entity.RecentFileEntity
import com.iguana.data.local.repository.RecentFileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecentFilesViewModel @Inject constructor(
    private val recentFileRepository: RecentFileRepository
) : ViewModel() {

    private val _recentFiles = MutableStateFlow<List<RecentFileEntity>>(emptyList())
    val recentFiles: StateFlow<List<RecentFileEntity>> = _recentFiles.asStateFlow()

    init {
        loadRecentFiles()
        addDummyData()
    }

    private fun loadRecentFiles() {
        viewModelScope.launch {
            recentFileRepository.getRecentFiles().collect { files ->
                _recentFiles.value = files
            }
        }
    }

    private fun addDummyData() {
        viewModelScope.launch {
            val dummyFiles = listOf(
                RecentFileEntity("1", "프로젝트 계획서.pdf", "file:///dummy/path1", System.currentTimeMillis(), 1),
                RecentFileEntity("2", "회의록.docx", "file:///dummy/path2", System.currentTimeMillis() - 86400000, 3),
                RecentFileEntity("3", "분기별 보고서.xlsx", "file:///dummy/path3", System.currentTimeMillis() - 172800000, null),
                RecentFileEntity("4", "제품 매뉴얼.pdf", "file:///dummy/path4", System.currentTimeMillis() - 259200000, 5)
            )
            dummyFiles.forEach { recentFileRepository.insertRecentFile(it.id, it.fileName, it.fileUri) }
        }
    }


    fun openFile(id: String, fileName: String, fileUri: String) {
        viewModelScope.launch {
            recentFileRepository.insertRecentFile(id, fileName, fileUri)
        }
    }

    fun updateBookmark(fileId: String, bookmarkedPage: Int) {
        viewModelScope.launch {
            recentFileRepository.updateBookmark(fileId, bookmarkedPage)
        }
    }

    fun cleanupOldFiles(daysToKeep: Int = 30) {
        viewModelScope.launch {
            recentFileRepository.cleanupOldFiles(daysToKeep)
        }
    }
}