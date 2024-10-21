package com.iguana.data.di

import com.iguana.data.BuildConfig
import com.iguana.data.remote.api.AnnotationApi
import com.iguana.data.remote.api.DocumentApi
import com.iguana.data.remote.api.SummarizeApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
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
}
