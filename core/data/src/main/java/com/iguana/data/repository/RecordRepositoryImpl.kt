package com.iguana.data.repository

import android.util.Log
import com.iguana.data.local.dao.PageTurnEventDao
import com.iguana.data.local.files.RecordingFileStorage
import com.iguana.data.mapper.toDomain
import com.iguana.data.mapper.toEntity
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
    private val localStorage: RecordingFileStorage,  // 로컬 스토리지 처리 클래스
    private val pageTurnEventDao: PageTurnEventDao
) : RecordRepository {

    // 서버에 녹음 파일 업로드
    override suspend fun uploadRecordingFile(recordingFile: RecordingFile): RecordingFile {
        return withContext(Dispatchers.IO) {
            val uploadRequest = recordingFile.toUploadRequestDto()
            Log.d("RecordRepositoryImpl", "Upload request 생성 완료: $uploadRequest")
            val response = recordApi.uploadRecording(
                recordingFile.documentId ?: throw AppError.NullResponseError("Document ID가 없습니다."),
                uploadRequest
            )
            Log.d("RecordRepositoryImpl", "API 응답 상태: ${response.isSuccessful}")

            if (response.isSuccessful) {
                Log.d("RecordRepositoryImpl", "API 응답 상태: ${response.isSuccessful}")
                val body =
                    response.body() ?: throw AppError.NullResponseError("녹음 파일 업로드 응답이 비어 있습니다.")
                recordingFile.updateWithResponse(body)
            } else {
                Log.e("RecordRepositoryImpl", "API 요청 실패: 코드 ${response.code()}, 메시지 ${response.message()}")
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
    override suspend fun deleteRecordingFile(filePath: String) {
        return withContext(Dispatchers.IO) {
            if (localStorage.isFileExists(filePath)) {
                val file = File(filePath)
                file.delete()
            } else {
                throw AppError.FileNotFound
            }
        }
    }

    // 서버에 페이지 이동 이벤트 업로드
    override suspend fun uploadPageTurnEvents(recordingId: Long, documentId: Long, events: List<PageTurnEvent>) {
        return withContext(Dispatchers.IO) {
            val requestDto = events.toPageTurnEventRequestDto(recordingId)  // 도메인 모델을 DTO로 변환
            val response = recordApi.recordPageTurnEvent(recordingId, requestDto)

            if (!response.isSuccessful) {
                throw AppError.PageTurnEventUploadFailed(response.code())
            } else {
                deletePageTurnEvents(documentId) // 업로드 후 삭제
            }
        }
    }

    // 로컬 스토리지에 페이지 이동 이벤트 저장
    override suspend fun savePageTurnEvents(documentId: Long, event: PageTurnEvent) {
        withContext(Dispatchers.IO) {
                pageTurnEventDao.insert(event.toEntity(documentId))
        }
    }

    // 로컬에 저장된 페이지 이동 이벤트 삭제
    override suspend fun deletePageTurnEvents(documentId: Long) {
        withContext(Dispatchers.IO) {
            pageTurnEventDao.deleteEventsByDocumentId(documentId)  // 페이지 이동 이벤트 삭제
        }
    }

    // 로컬에서 모든 페이지 이동 이벤트를 로드
    override suspend fun loadPageTurnEvents(documentId: Long): List<PageTurnEvent> {
        return withContext(Dispatchers.IO) {
            pageTurnEventDao.getEventsByDocumentId(documentId).map {
                it.toDomain()
            }
        }
    }
}

