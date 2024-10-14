package com.iguana.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.iguana.data.remote.model.PageTurnEventDto
import com.iguana.data.remote.model.PageTurnEventRequestDto
import com.iguana.data.remote.model.RecordingUploadRequestDto
import com.iguana.data.remote.model.RecordingUploadResponseDto
import com.iguana.domain.model.record.PageTurnEvent
import com.iguana.domain.model.record.RecordingFile
import java.io.File
import java.util.Base64

// PageTurnEvent(doain)를 PageTurnEventRequestDto(data)로 변환하는 함수 (페이지 넘김 이벤트)
fun List<PageTurnEvent>.toPageTurnEventRequestDto(recordingId: Long): PageTurnEventRequestDto {
    return PageTurnEventRequestDto(
        recordingId = recordingId,
        events = this.toPageTurnEventDtoList()
    )
}

fun List<PageTurnEvent>.toPageTurnEventDtoList(): List<PageTurnEventDto> {
    return this.map { event ->
        PageTurnEventDto(
            prevPage = event.pageNumber - 1,
            nextPage = event.pageNumber,
            timestamp = event.timestamp.toDouble()
        )
    }
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


// 서버 응답을 기존의 RecordingFile에 덮어씌우는 매퍼
fun RecordingFile.updateWithResponse(response: RecordingUploadResponseDto): RecordingFile {
    return this.copy(
        recordingId = response.recordingId ?: this.recordingId,
        documentId = response.documentId ?: this.documentId,
    )
}