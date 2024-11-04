package com.iguana.data.local.files

import android.util.Log
import com.arthenica.mobileffmpeg.FFmpeg
import com.iguana.data.remote.model.PageTurnEventDto
import com.iguana.domain.utils.AppError
import java.io.File
import javax.inject.Inject

class RecordingFileStorage @Inject constructor(
    private val baseDir: File
) {

    // 로컬 스토리지에 녹음 파일 저장
    fun saveRecordingFile(file: File) {
        val destination = File(baseDir, file.name)
        file.copyTo(destination, overwrite = true)
    }

    // 파일 존재 여부 확인 함수
    fun isFileExists(filePath: String): Boolean {
        val file = File(filePath)
        return file.exists()
    }

    // 3gp 파일을 mp3로 변환하고 변환된 파일 저장
    fun convertAndSave3gpToMp3(inputFilePath: String, outputFileName: String): File {
        // 기존 확장자를 제거하고 ".mp3" 확장자를 추가
        val sanitizedOutputFileName = outputFileName.substringBeforeLast('.') + ".mp3"
        val outputFilePath = "$baseDir/$sanitizedOutputFileName"

        val command = "-i \"$inputFilePath\" -vn -ar 44100 -ac 2 -b:a 192k \"$outputFilePath\""

        val result = FFmpeg.execute(command)


        if (result != 0) {
            Log.e("RecordingFileStorage", "변환 실패: $result")
            throw AppError.ConversionFailed("3gp 파일을 mp3로 변환하는데 실패했습니다.")
        }

        val outputFile = File(outputFilePath)
        if (!outputFile.exists()) {
            Log.e("RecordingFileStorage", "변환된 파일이 존재하지 않습니다.")
            throw AppError.FileNotFound
        }

        Log.d("RecordingFileStorage", "Input file path: $inputFilePath")
        Log.d("RecordingFileStorage", "Output file path: $outputFilePath")


        return outputFile
    }
}
