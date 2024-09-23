package com.iguana.data.di

import com.iguana.data.repository.LoginRepositoryImpl
import com.iguana.data.repository.LoginRepository
import com.iguana.data.repository.SharedPreferencesHelper
import com.iguana.data.repository.SharedPreferencesHelperImpl
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
    abstract fun bindLoginRepository(
        loginRepositoryImpl: LoginRepositoryImpl
    ): LoginRepository

    @Binds
    @Singleton
    abstract fun bindSharedPreferencesHelper(
        sharedPreferencesHelperImpl: SharedPreferencesHelperImpl
    ): SharedPreferencesHelper
}