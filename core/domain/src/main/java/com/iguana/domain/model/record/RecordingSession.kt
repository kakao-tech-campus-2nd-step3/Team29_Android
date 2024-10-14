package com.iguana.domain.model.record

// 녹음 전, 녹음 도중 사용할 녹음 세션 정보
data class RecordingSession(
    val sessionId: String,           // 녹음 세션 ID
    val documentId: Long,            // 연관된 문서 ID
    var recordingFilePath: String,   // 녹음된 파일 경로 (녹음이 끝나면 설정됨)
    var startTime: Long,             // 녹음 시작 시간 (Unix timestamp)
    var duration: Long = 0L,         // 녹음 길이 (밀리초)
    var state: RecordingState = RecordingState.IDLE, // 현재 녹음 상태
    val documentName: String         // 연관된 문서 이름
)
