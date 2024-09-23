package com.iguana.data.di

import com.iguana.data.repository.LoginRepositoryImpl
import com.iguana.data.repository.LoginRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        loginRepositoryImpl: LoginRepositoryImpl
    ): LoginRepository
}