package com.iguana.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface LoginApi {
    @POST("api/members/oauth/login/{oauthProvider}")
    suspend fun sendKakaoToken(
        @Path("oauthProvider") oauthProvider: String = "KAKAO",
        @Body tokenRequest: KakaoTokenRequest
    ): Response<LoginResponse>

    @POST("api/members/token/refresh")
    suspend fun refreshToken(
        @Body refreshTokenRequest: TokenRefreshRequest
    ): Response<LoginResponse>

    @GET("api/members/me")
    suspend fun getUserProfile(): Response<UserProfileResponse>
}

data class KakaoTokenRequest(val oauthAccessToken: String)

data class LoginResponse(val accessToken: String, val refreshToken: String)

data class TokenRefreshRequest(val refreshToken: String)

data class UserProfileResponse(val id: Long, val nickname: String)
