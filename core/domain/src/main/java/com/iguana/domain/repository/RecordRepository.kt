package com.iguana.domain.repository

import com.iguana.domain.model.record.PageTurnEvent
import com.iguana.domain.model.record.RecordingFile

interface RecordRepository {
    // 서버에 녹음 파일 업로드
    suspend fun uploadRecordingFile(recordingFile: RecordingFile): RecordingFile

    // 로컬 스토리지에 녹음 파일 저장
    suspend fun saveRecordingFile(recordingFile: RecordingFile)

    // 로컬에 있는 녹음 파일 삭제
    suspend fun deleteRecordingFile(filePath: String)

    // 페이지 이동 이벤트 업로드
    suspend fun uploadPageTurnEvents(documentId: Long, recordingId: Long, events: List<PageTurnEvent>)

    // 로컬 스토리지에 페이지 이동 이벤트 저장
    suspend fun savePageTurnEvents(recordingId: Long, event: PageTurnEvent)

    // 로컬에 저장된 페이지 이동 이벤트 삭제
    suspend fun deletePageTurnEvents(recordingId: Long)

    // 로컬에서 모든 페이지 이동 이벤트를 로드
    suspend fun loadPageTurnEvents(documentId: Long): List<PageTurnEvent>

}
