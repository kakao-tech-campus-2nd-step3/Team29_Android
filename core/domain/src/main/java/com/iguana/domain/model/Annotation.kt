package com.iguana.domain.model

data class Annotation(
    val id: Long, // 고유 식별자
    val pageNumber: Int,
    val content: String, // 내용
    val x: Float, // x 좌표
    val y: Float, // y 좌표
    val width: Float, // 너비
    val height: Float, // 높이
    val syncStatus: SyncStatus = SyncStatus.NOT_SYNCED // 동기화 상태 필드 추가, 기본값을 NOT_SYNCED로 설정
)

// 동기화 상태를 나타내는 Enum 클래스 정의
enum class SyncStatus {
    SYNCED,      // 서버와 동기화된 상태
    NOT_SYNCED,  // 아직 동기화되지 않은 상태
    FAILED       // 동기화가 실패한 상태
}