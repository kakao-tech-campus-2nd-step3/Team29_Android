package com.iguana.notetaking.recording

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import java.io.IOException

class RecordingService : Service() {

    private var recorder: MediaRecorder? = null
    private var isRecording = false
    private val channelId = "RecordingServiceChannel"
    private val notificationId = 1

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        Log.d("RecordingService", "onStartCommand: $action")

        when (action) {
            "START_RECORDING" -> startRecording()
            "STOP_RECORDING" -> stopRecording()
        }

        return START_STICKY
    }

    private fun startRecording() {
        Log.d("RecordingService", "녹음이 시작되기 바로 직전입니다.")
        if (isRecording) {
            Log.w("RecordingService", "녹음이 이미 진행 중입니다.")
            return
        }
        // 파일 경로 생성
        val outputFilePath = getRecordingFilePath(this)

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(outputFilePath)

            try {
                prepare()
                start()
                isRecording = true
                startForeground(notificationId, createRecordingNotification("녹음 중..."))
                Log.d("RecordingService", "녹음이 시작되었습니다.")
            } catch (e: IOException) {
                Log.e("RecordingService", "prepare() failed: ${e.message}")
            }
        }
    }

    private fun stopRecording() {
        if (!isRecording) {
            Log.w("RecordingService", "현재 녹음이 진행 중이지 않습니다.")
            return
        }

        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        isRecording = false
        stopForeground(true)
        stopSelf()
        Log.d("RecordingService", "녹음이 종료되었습니다.")
    }

    private fun createRecordingNotification(contentText: String): Notification {
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("녹음 서비스")
            .setContentText(contentText)
            .setSmallIcon(com.iguana.designsystem.R.drawable.ic_record_active)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            channelId,
            "녹음 서비스 채널",
            NotificationManager.IMPORTANCE_LOW
        )

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    fun getRecordingFilePath(context: Context): String {
        // 외부 저장소의 앱 전용 디렉토리
        val directory = context.getExternalFilesDir(null)
        return "${directory?.absolutePath}/recording_${System.currentTimeMillis()}.3gp"
    }
}
