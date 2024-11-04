package com.iguana.data.di

import com.iguana.data.BuildConfig
import com.iguana.data.remote.api.AnnotationApi
import com.iguana.data.remote.api.DocumentApi
import com.iguana.data.remote.api.RecordApi
import com.iguana.data.remote.api.SummarizeApi
import com.iguana.domain.repository.SharedPreferencesHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // 로그 설정 추가
    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(sharedPreferencesHelper: SharedPreferencesHelper): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sharedPreferencesHelper))
            .addInterceptor(logging)
            // 연결 타임아웃 설정 (예: 30초)
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            // 읽기 타임아웃 설정 (예: 30초)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            // 쓰기 타임아웃 설정 (예: 30초)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideLoginApi(retrofit: Retrofit): com.iguana.data.remote.api.LoginApi {
        return retrofit.create(com.iguana.data.remote.api.LoginApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDocumentApi(retrofit: Retrofit): DocumentApi {
        return retrofit.create(DocumentApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAnnotationApi(retrofit: Retrofit): AnnotationApi {
        return retrofit.create(AnnotationApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSummarizeApi(retrofit: Retrofit): SummarizeApi {
        return retrofit.create(SummarizeApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRecordApi(retrofit: Retrofit): RecordApi {
        return retrofit.create(RecordApi::class.java)
    }
}
