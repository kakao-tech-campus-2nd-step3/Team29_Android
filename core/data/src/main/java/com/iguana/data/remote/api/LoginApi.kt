package com.iguana.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface LoginApi {
    @GET("api/members/login/kakao")
    suspend fun getKakaoLoginUrl(): Response<KakaoLoginUrlResponse>

    @POST("api/members/login/kakao/callback")
    suspend fun sendKakaoToken(@Body tokenRequest: KakaoTokenRequest): Response<LoginResponse>
}

data class KakaoLoginUrlResponse(val url: String)

data class KakaoTokenRequest(val token: String)

data class LoginResponse(val accessToken: String, val refreshToken: String)
