package com.iguana.domain.repository



interface LoginRepository {
    suspend fun getKakaoLoginUrl(): String?
    suspend fun sendKakaoToken(token: String): Boolean
    fun getAccessToken(): String?
    fun isLoggedIn(): Boolean
}