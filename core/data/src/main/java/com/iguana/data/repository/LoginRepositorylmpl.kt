package com.iguana.data.repository

import android.util.Log
import com.iguana.data.remote.api.LoginApi
import com.iguana.data.remote.api.KakaoTokenRequest
import com.iguana.data.remote.api.TokenRefreshRequest
import com.iguana.domain.model.UserProfile
import com.iguana.domain.repository.LoginRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepositoryImpl @Inject constructor(
    private val loginApi: LoginApi,
    private val sharedPreferencesHelper: com.iguana.domain.repository.SharedPreferencesHelper
) : LoginRepository {

    override suspend fun sendKakaoToken(oauthAccessToken: String): Boolean {
        Log.d(TAG, "Sending Kakao Token: $oauthAccessToken")
        return try {
            val tokenRequest = KakaoTokenRequest(oauthAccessToken)
            val response = loginApi.sendKakaoToken(tokenRequest = tokenRequest)
            if (response.isSuccessful) {
                response.body()?.let { loginResponse ->
                    sharedPreferencesHelper.saveTokens(loginResponse.accessToken, loginResponse.refreshToken)
                    true
                } ?: run {
                    Log.e(TAG, "카카오 토큰 전송 성공했지만 응답 본문이 비어있음")
                    false
                }
            } else {
                Log.e(TAG, "카카오 토큰 전송 실패: ${response.code()} - ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "카카오 토큰 전송 중 예외 발생: ${e.message}", e)
            false
        }
    }

    override suspend fun refreshToken(refreshToken: String): Boolean {
        return try {
            val response = loginApi.refreshToken(TokenRefreshRequest(refreshToken))
            if (response.isSuccessful) {
                response.body()?.let { loginResponse ->
                    sharedPreferencesHelper.saveTokens(loginResponse.accessToken, loginResponse.refreshToken)
                    true
                } ?: run {
                    Log.e(TAG, "토큰 리프레쉬 성공했지만 응답 본문이 비어있음")
                    false
                }
            } else {
                Log.e(TAG, "토큰 리프레쉬 실패: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "토큰 리프레쉬 중 예외 발생: ${e.message}", e)
            false
        }
    }

    override suspend fun getUserProfile(): UserProfile? {
        return try {
            val response = loginApi.getUserProfile()
            if (response.isSuccessful) {
                response.body()?.let { dataProfile ->
                    UserProfile(
                        id = dataProfile.id,
                        nickname = dataProfile.nickname
                    )
                }
            } else {
                Log.e(TAG, "사용자 프로필 조회 실패: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "사용자 프로필 조회 중 예외 발생: ${e.message}", e)
            null
        }
    }

    override fun getAccessToken(): String? {
        return sharedPreferencesHelper.getAccessToken()
    }

    override fun getRefreshToken(): String? {
        return sharedPreferencesHelper.getRefreshToken()
    }

    override fun isLoggedIn(): Boolean {
        return sharedPreferencesHelper.isLoggedIn()
    }

    companion object {
        private const val TAG = "LoginRepositoryImpl"
    }
}