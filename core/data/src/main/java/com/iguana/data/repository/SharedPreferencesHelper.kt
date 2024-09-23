package com.iguana.data.repository

interface SharedPreferencesHelper {
    fun saveTokens(accessToken: String, refreshToken: String)
    fun getAccessToken(): String?
    fun isLoggedIn(): Boolean
}