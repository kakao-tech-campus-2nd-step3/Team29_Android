package com.iguana.data.local.files

import com.iguana.data.remote.model.PageTurnEventDto
import com.iguana.domain.utils.AppError
import java.io.File
import javax.inject.Inject

class RecordingFileStorage @Inject constructor(
    private val baseDir: File,  // 내부 저장소 경로 (context.filesDir 등을 사용할 수 있음)
) {

    // 로컬 스토리지에 녹음 파일 저장
    fun saveRecordingFile(file: File): Result<Unit> {
        return try {
            val destination = File(baseDir, file.name)
            file.copyTo(destination, overwrite = true)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 페이지 이동 이벤트 저장
    fun savePageTurnEvents(recordingId: Long, events: List<PageTurnEventDto>): Result<Unit> {
        return try {
            val file = File(baseDir, "page_turn_events_$recordingId.txt")
            file.writeText(events.joinToString(separator = "\n") { event ->
                "${event.prevPage},${event.nextPage},${event.timestamp}"
            })
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 로컬 스토리지에서 페이지 이동 이벤트 삭제
    fun deletePageTurnEvents(recordingId: Long): Result<Unit> {
        return try {
            val file = File(baseDir, "page_turn_events_$recordingId.txt")
            if (file.exists()) {
                file.delete()
                Result.success(Unit)
            } else {
                Result.failure(AppError.FileNotFound)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
