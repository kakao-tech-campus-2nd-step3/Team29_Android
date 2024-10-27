package com.iguana.data.mapper

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
        events = this.map { event ->
            PageTurnEventDto(
                prevPage = event.prevPage,
                nextPage = event.nextPage,
                timestamp = event.timestamp
            )
        }
    )
}

// PageTurnEvent(DTO List) -> PageTurnEvent(Domain List)

fun List<PageTurnEventDto>.toPageTurnEventDomainList(documentId: Long): List<PageTurnEvent> {
    return this.map { dto ->
        PageTurnEvent(
            documentId = documentId,
            prevPage = dto.prevPage,
            nextPage = dto.nextPage,
            timestamp = dto.timestamp
        )
    }
}

// PageTurnEvents(Domain List) -> PageTurnEvents(DTO List)
fun List<PageTurnEvent>.toPageTurnEventDtoList(): List<PageTurnEventDto> {
    return this.map { event ->
        PageTurnEventDto(
            prevPage = event.prevPage,
            nextPage = event.nextPage,
            timestamp = event.timestamp.toDouble()
        )
    }
}

// RecordingFile을 RecordingUploadRequestDto로 변환하는 함수 (녹음 파일 업로드)
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