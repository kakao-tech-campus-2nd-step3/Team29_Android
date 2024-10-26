package com.iguana.data.local.files

import android.util.Log
import com.iguana.data.remote.model.PageTurnEventDto
import com.iguana.domain.utils.AppError
import java.io.File
import javax.inject.Inject

class RecordingFileStorage @Inject constructor(
    private val baseDir: File
) {

    // 로컬 스토리지에 녹음 파일 저장
    fun saveRecordingFile(file: File) {
        val destination = File(baseDir, file.name)
        file.copyTo(destination, overwrite = true)
    }

    // 페이지 이동 이벤트 저장
    fun savePageTurnEvents(documentId: Long, events: List<PageTurnEventDto>) {
        val file = File(baseDir, "page_turn_events_$documentId.txt")
        // 기존 파일 내용에 이어서 새 이벤트를 추가
        file.appendText(events.joinToString(separator = "\n") { event ->
            "${event.prevPage},${event.nextPage},${event.timestamp}"
        } + "\n")
    }

    // 로컬 스토리지에서 페이지 이동 이벤트 삭제
    fun deletePageTurnEvents(documentId: Long) {
        val file = File(baseDir, "page_turn_events_$documentId.txt")
        if (file.exists()) {
            file.delete()
            Result.success(Unit)
        } else {
            throw AppError.FileNotFound
        }
    }

    // 파일 존재 여부 확인 함수
    fun isFileExists(filePath: String): Boolean {
        val file = File(filePath)
        return file.exists()
    }
}
