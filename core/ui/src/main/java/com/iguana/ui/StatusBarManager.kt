package com.iguana.ui

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowInsetsController
import androidx.core.view.WindowCompat

object StatusBarManager {
    fun setTransparentStatusBar(activity: Activity) {
        activity.window.statusBarColor = Color.WHITE // 상태바 색상을 흰색으로 설정

        // 전체 화면 설정
        WindowCompat.setDecorFitsSystemWindows(activity.window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 상태바 아이콘을 밝은 모드로 설정 (흰색 상태바 아이콘)
            activity.window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            @Suppress("DEPRECATION")
            activity.window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    )
        }
    }
}
