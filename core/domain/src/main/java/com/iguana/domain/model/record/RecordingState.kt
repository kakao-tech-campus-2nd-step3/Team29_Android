package com.iguana.domain.model.record

enum class RecordingState {
    IDLE,        // 녹음 대기 상태 (녹음 시작 전)
    RECORDING,   // 녹음 중
    PAUSED,      // 녹음 일시 중지 상태
    STOPPED      // 녹음 중지 상태 (녹음 완료)
}