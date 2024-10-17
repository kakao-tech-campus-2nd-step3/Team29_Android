package com.iguana.data.utils

import android.util.Log

object Logger {
    private const val DEFAULT_TAG = "AppLogger"

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        Log.e(tag, message, throwable)
    }

    fun i(tag: String, message: String) {
        Log.i(tag, message)
    }

    fun d(tag: String, message: String) {
        Log.d(tag, message)
    }

    fun w(tag: String, message: String) {
        Log.w(tag, message)
    }
}
