package com.iguana.notetaking.recording

enum class RecordingStatus {
    NOT_STARTED,  // 녹음 한 번도 안 해본 상태
    RECORDING,    // 녹음 중인 상태
    COMPLETED     // 녹음 완료 상태 (녹음 중 아님)
}