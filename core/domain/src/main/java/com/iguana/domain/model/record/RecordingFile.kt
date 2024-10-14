package com.iguana.domain.model.record

// 녹음이 종료되고 저장된 파일 정보
data class RecordingFile(
    val filePath: String,      // 파일 경로
    val fileSize: Long,        // 파일 크기 (바이트)
    val format: String,        // 파일 포맷 (예: "mp3", "wav")
    val duration: Long,        // 녹음 길이 (밀리초)
    val documentName: String,   // 연관된 문서 이름
    var recordingId: Long? = null,    // 서버에서 반환된 녹음 ID
    var documentId: Long? = null,     // 서버에서 반환된 문서 ID
)
