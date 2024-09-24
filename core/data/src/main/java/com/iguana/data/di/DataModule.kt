package com.iguana.data.di

import com.iguana.data.repository.LoginRepositoryImpl
import com.iguana.domain.repository.LoginRepository
import com.iguana.domain.repository.SharedPreferencesHelper
import com.iguana.data.local.db.SharedPreferencesHelperImpl
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
    ): com.iguana.domain.repository.LoginRepository

    @Binds
    @Singleton
    abstract fun bindSharedPreferencesHelper(
        sharedPreferencesHelperImpl: SharedPreferencesHelperImpl
    ): com.iguana.domain.repository.SharedPreferencesHelper
}