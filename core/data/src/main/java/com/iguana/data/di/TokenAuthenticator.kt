package com.iguana.data.di

import com.iguana.data.BuildConfig
import com.iguana.data.remote.api.LoginApi
import com.iguana.data.remote.api.TokenRefreshRequest
import com.iguana.data.remote.api.LoginResponse
import com.iguana.domain.repository.SharedPreferencesHelper
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val sharedPreference: SharedPreferencesHelper
) : Authenticator {

    // 토큰 갱신용 별도 Retrofit 인스턴스 생성
    private val tokenApi = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .client(OkHttpClient.Builder().build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(LoginApi::class.java)

    override fun authenticate(route: Route?, response: Response): Request? {
        // 이미 재시도했다면 null 반환
        if (response.request.header("Retry-With-New-Token") != null) {
            return null
        }

        return runBlocking {
            val refreshToken = sharedPreference.getRefreshToken() ?: return@runBlocking null
            
            try {
                val tokenResponse = tokenApi.refreshToken(TokenRefreshRequest(refreshToken))
                if (tokenResponse.isSuccessful) {
                    tokenResponse.body()?.let { loginResponse ->
                        sharedPreference.saveTokens(
                            loginResponse.accessToken,
                            loginResponse.refreshToken
                        )
                        
                        // 새로운 토큰으로 요청 재시도
                        response.request.newBuilder()
                            .header("Authorization", "Bearer ${loginResponse.accessToken}")
                            .header("Retry-With-New-Token", "true")
                            .build()
                    }
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
} 