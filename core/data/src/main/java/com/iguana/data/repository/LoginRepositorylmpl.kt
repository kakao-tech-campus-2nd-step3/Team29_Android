package com.iguana.data.repository

import android.util.Log
import com.iguana.data.remote.api.LoginApi
import com.iguana.data.remote.api.KakaoTokenRequest
import com.iguana.domain.repository.LoginRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepositoryImpl @Inject constructor(
    private val loginApi: LoginApi,
    private val sharedPreferencesHelper: com.iguana.domain.repository.SharedPreferencesHelper
) : LoginRepository {

    override suspend fun getKakaoLoginUrl(): String? {
        return try {
            val response = loginApi.getKakaoLoginUrl()
            if (response.isSuccessful) {
                response.body()?.url
            } else {
                Log.e(TAG, "카카오 로그인 URL 가져오기 실패: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "카카오 로그인 URL 가져오기 중 예외 발생: ${e.message}", e)
            null
        }
    }

    override suspend fun sendKakaoToken(token: String): Boolean {
        return try {
            val response = loginApi.sendKakaoToken(KakaoTokenRequest(token))
            if (response.isSuccessful) {
                response.body()?.let { loginResponse ->
                    sharedPreferencesHelper.saveTokens(loginResponse.accessToken, loginResponse.refreshToken)
                    true
                } ?: run {
                    Log.e(TAG, "카카오 토큰 전송 성공했지만 응답 본문이 비어있음")
                    false
                }
            } else {
                Log.e(TAG, "카카오 토큰 전송 실패: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "카카오 토큰 전송 중 예외 발생: ${e.message}", e)
            false
        }
    }

    override fun getAccessToken(): String? {
        return sharedPreferencesHelper.getAccessToken()
    }

    override fun isLoggedIn(): Boolean {
        return sharedPreferencesHelper.isLoggedIn()
    }

    companion object {
        private const val TAG = "LoginRepositoryImpl"
    }
}