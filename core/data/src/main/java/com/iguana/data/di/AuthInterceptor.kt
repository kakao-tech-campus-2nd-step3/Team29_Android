package com.iguana.data.di

import com.iguana.domain.repository.SharedPreferencesHelper
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val sharedPreference: SharedPreferencesHelper) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sharedPreference.getAccessToken()
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }
}
