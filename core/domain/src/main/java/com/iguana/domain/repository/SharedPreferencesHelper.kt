package com.iguana.domain.repository

interface SharedPreferencesHelper {
    fun saveTokens(accessToken: String, refreshToken: String)
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun isLoggedIn(): Boolean
}
