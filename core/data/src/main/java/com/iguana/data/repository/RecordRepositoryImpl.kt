package com.iguana.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.iguana.data.local.files.RecordingFileStorage
import com.iguana.data.mapper.toPageTurnEventDtoList
import com.iguana.data.mapper.toPageTurnEventRequestDto
import com.iguana.data.mapper.toUploadRequestDto
import com.iguana.data.mapper.updateWithResponse
import com.iguana.data.remote.api.RecordApi
import com.iguana.domain.model.record.PageTurnEvent
import com.iguana.domain.model.record.RecordingFile
import com.iguana.domain.repository.RecordRepository
import com.iguana.domain.utils.AppError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class RecordRepositoryImpl @Inject constructor(
    private val recordApi: RecordApi,  // 서버 통신을 위한 API 인터페이스
    private val localStorage: RecordingFileStorage  // 로컬 스토리지 처리 클래스
) : RecordRepository {

    // 서버에 녹음 파일 업로드
    override suspend fun uploadRecordingFile(recordingFile: RecordingFile): Result<RecordingFile> {
        return withContext(Dispatchers.IO) {
            try {
                val uploadRequest = recordingFile.toUploadRequestDto()  // 도메인 모델을 DTO로 변환
                val response = recordApi.uploadRecording(recordingFile.documentName.toLong(), uploadRequest)

                if (response.isSuccessful) {
                    response.body()?.let {
                        // 서버 응답을 기존 RecordingFile에 덮어씌움
                        val updatedFile = recordingFile.updateWithResponse(it)
                        Result.success(updatedFile)
                    } ?: Result.failure(AppError.UploadFailed)
                } else {
                    Result.failure(AppError.UploadFailed)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }


    // 로컬 스토리지에 녹음 파일 저장
    override suspend fun saveRecordingFile(recordingFile: RecordingFile): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(recordingFile.filePath)
                if (file.exists()) {
                    Result.success(Unit)  // 파일이 이미 존재하면 성공 반환
                } else {
                    localStorage.saveRecordingFile(file)  // 로컬 스토리지에 저장
                    Result.success(Unit)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // 로컬에 있는 녹음 파일 삭제
    override suspend fun deleteRecordingFile(recordingFile: RecordingFile): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(recordingFile.filePath)
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

    // 서버에 페이지 이동 이벤트 업로드
    override suspend fun uploadPageTurnEvents(recordingId: Long, events: List<PageTurnEvent>): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val requestDto = events.toPageTurnEventRequestDto(recordingId)  // 도메인 모델을 DTO로 변환
                val response = recordApi.recordPageTurnEvent(recordingId, requestDto)

                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("페이지 이동 이벤트 업로드 실패: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // 로컬 스토리지에 페이지 이동 이벤트 저장
    override suspend fun savePageTurnEvents(recordingId: Long, events: List<PageTurnEvent>): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                localStorage.savePageTurnEvents(recordingId, events.toPageTurnEventDtoList())  // 페이지 이동 이벤트 로컬에 저장
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // 로컬에 저장된 페이지 이동 이벤트 삭제
    override suspend fun deletePageTurnEvents(recordingId: Long): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                localStorage.deletePageTurnEvents(recordingId)  // 페이지 이동 이벤트 삭제
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
