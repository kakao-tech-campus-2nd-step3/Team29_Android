package com.iguana.notai

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NotaiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, getString(R.string.kakao_native_key))

        // 키해시를 구해서 로그에 출력
        val keyHash = Utility.getKeyHash(this)
        Log.d("KeyHash", "KeyHash: $keyHash")
    }
}