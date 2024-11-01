package com.iguana.notetaking.recording

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.io.IOException


class RecordingService : Service() {

    private var recorder: MediaRecorder? = null
    private var isRecording = false
    private val channelId = "RecordingServiceChannel"
    private val notificationId = 1
    private var outputFilePath: String? = null

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

        // 권한 확인
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("RecordingService", "녹음 권한이 없습니다.")
            return
        }

        outputFilePath = getRecordingFilePath(this)
        if (outputFilePath == null) {
            Log.e("RecordingService", "녹음 파일 경로를 생성할 수 없습니다.")
            return
        }

        recorder = MediaRecorder().apply {
            try {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(outputFilePath)

                prepare()
                start()
                isRecording = true
                startForeground(notificationId, createRecordingNotification("녹음 중..."))
            } catch (e: IllegalStateException) {
                Log.e("RecordingService", "설정 오류: ${e.message}")
                releaseRecorder()
            } catch (e: IOException) {
                Log.e("RecordingService", "prepare() 실패: ${e.message}")
                releaseRecorder()
            }
        }
    }

    private fun releaseRecorder() {
        recorder?.release()
        recorder = null
        isRecording = false
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
        sendRecordingFinishedBroadcast()
        stopSelf()
    }

    private fun createRecordingNotification(contentText: String): Notification {
        return NotificationCompat.Builder(this, channelId).setContentTitle("녹음 서비스")
            .setContentText(contentText)
            .setSmallIcon(com.iguana.designsystem.R.drawable.ic_record_active)
            .setPriority(NotificationCompat.PRIORITY_LOW).build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            channelId, "녹음 서비스 채널", NotificationManager.IMPORTANCE_LOW
        )

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun getRecordingFilePath(context: Context): String {
        // 외부 저장소의 앱 전용 디렉토리
        val directory = context.getExternalFilesDir(null)
        Log.d("RecordingService", "녹음 파일 경로: ${directory?.absolutePath}")
        return "${directory?.absolutePath}/recording_${System.currentTimeMillis()}.3gp"
    }

    private fun sendRecordingFinishedBroadcast() {
        outputFilePath?.let { path ->
            val intent = Intent("com.iguana.notetaking.RECORDING_FINISHED").apply {
                putExtra("filePath", path)
                putExtra("fileName", path.substringAfterLast("/"))
            }
            sendBroadcast(intent)
        }
    }

}
