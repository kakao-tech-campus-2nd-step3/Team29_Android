package com.iguana.domain.utils

import android.net.Uri
import java.io.File

interface FileHelper {
    fun copyFileToInternalStorage(uri: Uri, fileName: String): Uri?
    fun getFileFromUri(uri: Uri): File?
}