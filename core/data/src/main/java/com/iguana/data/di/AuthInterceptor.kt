package com.iguana.data.di

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenProvider: () -> String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenProvider()
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }
}
