package com.iguana.data.local.files

import android.content.Context
import android.net.Uri
import com.iguana.domain.utils.FileHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject

class FileHelperImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : FileHelper {
    override fun copyFileToInternalStorage(uri: Uri, fileName: String): Uri? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputDirectory = File(context.filesDir, "documents")

            if (!outputDirectory.exists()) {
                outputDirectory.mkdir()
            }

            val outputFile = File(outputDirectory, fileName)

            inputStream?.use { input ->
                FileOutputStream(outputFile).use { output ->
                    input.copyTo(output)
                }
            }

            // 복사된 파일의 내부 URI 반환
            Uri.fromFile(outputFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getFileFromUri(uri: Uri): File? {
        return uri.path?.let { path ->
            File(path).takeIf { it.exists() }
        }
    }
}