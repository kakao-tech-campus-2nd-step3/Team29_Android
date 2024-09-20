package com.iguana.domain.repository

interface AuthRepository {
    suspend fun getKakaoLoginUrl(): String?
    suspend fun sendKakaoToken(token: String): Boolean
    fun getAccessToken(): String?
    fun isLoggedIn(): Boolean
}