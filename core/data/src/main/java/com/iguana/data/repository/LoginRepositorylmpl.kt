package com.iguana.data.repository

import com.iguana.data.remote.api.LoginApi
import com.iguana.data.remote.api.KakaoTokenRequest
import com.iguana.domain.repository.LoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepositoryImpl @Inject constructor(
    private val loginApi: LoginApi,
    private val sharedPreferencesHelper: com.iguana.domain.repository.SharedPreferencesHelper
) : LoginRepository {

    override suspend fun getKakaoLoginUrl(): String? = withContext(Dispatchers.IO) {
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

    override suspend fun sendKakaoToken(token: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = loginApi.sendKakaoToken(KakaoTokenRequest(token))
            if (response.isSuccessful) {
                response.body()?.let { loginResponse ->
                    sharedPreferencesHelper.saveTokens(loginResponse.accessToken, loginResponse.refreshToken)
                    true
                } ?: false
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    override fun getAccessToken(): String? {
        return sharedPreferencesHelper.getAccessToken()
    }

    override fun isLoggedIn(): Boolean {
        return sharedPreferencesHelper.isLoggedIn()
    }
}