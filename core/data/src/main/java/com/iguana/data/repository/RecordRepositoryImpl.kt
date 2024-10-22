package com.iguana.data.repository

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
    override suspend fun uploadRecordingFile(recordingFile: RecordingFile): RecordingFile {
        return withContext(Dispatchers.IO) {
            val uploadRequest = recordingFile.toUploadRequestDto()
            val response = recordApi.uploadRecording(recordingFile.documentName.toLong(), uploadRequest)

            if (response.isSuccessful) {
                val body = response.body() ?: throw AppError.NullResponseError("녹음 파일 업로드 응답이 비어 있습니다.")
                return@withContext recordingFile.updateWithResponse(body)
            } else {
                throw AppError.UploadFailed
            }
        }
    }


    // 로컬 스토리지에 녹음 파일 저장
    override suspend fun saveRecordingFile(recordingFile: RecordingFile) {
        return withContext(Dispatchers.IO) {
            if (!localStorage.isFileExists(recordingFile.filePath)) {
                val file = File(recordingFile.filePath)
                localStorage.saveRecordingFile(file)
            }
        }
    }

    // 로컬에 있는 녹음 파일 삭제
    override suspend fun deleteRecordingFile(recordingFile: RecordingFile) {
        return withContext(Dispatchers.IO) {
            if (localStorage.isFileExists(recordingFile.filePath)) {
                val file = File(recordingFile.filePath)
                file.delete()
            } else {
                throw AppError.FileNotFound
            }
        }
    }

    // 서버에 페이지 이동 이벤트 업로드
    override suspend fun uploadPageTurnEvents(recordingId: Long, events: List<PageTurnEvent>) {
        return withContext(Dispatchers.IO) {
            val requestDto = events.toPageTurnEventRequestDto(recordingId)  // 도메인 모델을 DTO로 변환
            val response = recordApi.recordPageTurnEvent(recordingId, requestDto)

            if (!response.isSuccessful) {
                throw AppError.PageTurnEventUploadFailed(response.code())
            }
        }
    }

    // 로컬 스토리지에 페이지 이동 이벤트 저장
    override suspend fun savePageTurnEvents(recordingId: Long, events: List<PageTurnEvent>) {
        withContext(Dispatchers.IO) {
            localStorage.savePageTurnEvents(
                recordingId,
                events.toPageTurnEventDtoList()
            )  // 페이지 이동 이벤트 로컬에 저장
        }
    }

    // 로컬에 저장된 페이지 이동 이벤트 삭제
    override suspend fun deletePageTurnEvents(recordingId: Long) {
        withContext(Dispatchers.IO) {
            localStorage.deletePageTurnEvents(recordingId)  // 페이지 이동 이벤트 삭제
        }
    }
}

