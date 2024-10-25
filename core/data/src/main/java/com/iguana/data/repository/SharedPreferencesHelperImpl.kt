package com.iguana.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.iguana.domain.repository.SharedPreferencesHelper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferencesHelperImpl @Inject constructor(
    context: Context
) : SharedPreferencesHelper {

    private val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    override fun saveTokens(accessToken: String, refreshToken: String) {
        prefs.edit()
            .putString("ACCESS_TOKEN", accessToken)
            .putString("REFRESH_TOKEN", refreshToken)
            .apply()
    }

    override fun getAccessToken(): String? {
        return prefs.getString("ACCESS_TOKEN", null)
    }

    override fun getRefreshToken(): String? {
        return prefs.getString("REFRESH_TOKEN", null)
    }

    override fun isLoggedIn(): Boolean {
        return getAccessToken() != null
    }
}