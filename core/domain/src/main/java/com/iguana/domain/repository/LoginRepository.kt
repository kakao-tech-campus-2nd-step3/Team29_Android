package com.iguana.domain.repository

import com.iguana.domain.model.UserProfile

interface LoginRepository {
    suspend fun sendKakaoToken(oauthAccessToken: String): Boolean
    suspend fun refreshToken(refreshToken: String): Boolean
    suspend fun getUserProfile(): UserProfile?
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun isLoggedIn(): Boolean
}
