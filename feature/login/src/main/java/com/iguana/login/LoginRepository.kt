package com.iguana.login

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class LoginRepository @Inject constructor(
    private val loginApi: com.iguana.data.remote.api.LoginApi,
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val PREFS_NAME = "login_prefs"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    suspend fun getKakaoLoginUrl(): String? = withContext(Dispatchers.IO) {
        try {
            val response = loginApi.getKakaoLoginUrl()
            if (response.isSuccessful) {
                response.body()?.url
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun sendKakaoToken(token: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = loginApi.sendKakaoToken(
                com.iguana.data.remote.api.KakaoTokenRequest(
                    token
                )
            )
            if (response.isSuccessful) {
                response.body()?.let { loginResponse ->
                    saveTokens(loginResponse)
                    true
                } ?: false
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun saveTokens(loginResponse: com.iguana.data.remote.api.LoginResponse) {
        sharedPreferences.edit().apply {
            putString(KEY_ACCESS_TOKEN, loginResponse.accessToken)
            putString(KEY_REFRESH_TOKEN, loginResponse.refreshToken)
            apply()
        }
    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
    }

    fun isLoggedIn(): Boolean {
        return getAccessToken() != null
    }
}
