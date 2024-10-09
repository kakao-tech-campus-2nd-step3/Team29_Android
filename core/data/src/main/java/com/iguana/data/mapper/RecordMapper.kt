package com.iguana.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.iguana.data.remote.model.PageTurnEventRequestDto
import com.iguana.data.remote.model.PageTurnEvent
import com.iguana.data.remote.model.RecordingUploadRequestDto
import com.iguana.domain.model.record.RecordingFile
import com.iguana.domain.model.record.RecordingSession
import java.io.File
import java.util.Base64

// RecordingSession을 PageTurnEventRequestDto로 변환하는 함수 (페이지 넘김 이벤트)
fun RecordingSession.toPageTurnEventRequestDto(events: List<PageTurnEvent>): PageTurnEventRequestDto {
    return PageTurnEventRequestDto(
        recordingId = this.sessionId.toLong(), // 녹음 세션 ID를 녹음 ID로 사용
        events = events
    )
}

// RecordingFile을 RecordingUploadRequestDto로 변환하는 함수 (녹음 파일 업로드)

@RequiresApi(Build.VERSION_CODES.O)
fun RecordingFile.toUploadRequestDto(): RecordingUploadRequestDto {
    // 파일을 Base64로 인코딩
    val fileContent = File(this.filePath).readBytes()
    val base64AudioData = Base64.getEncoder().encodeToString(fileContent)

    return RecordingUploadRequestDto(
        documentName = this.documentName,
        audioData = "data:audio/${this.format};base64,$base64AudioData"
    )
}
