package com.iguana.data.di

import com.iguana.data.repository.AnnotationRepositoryImpl
import com.iguana.data.repository.DocumentsRepositoryImpl
import com.iguana.data.repository.LoginRepositoryImpl
import com.iguana.data.repository.RecentFileRepositoryImpl
import com.iguana.domain.repository.AnnotationRepository
import com.iguana.domain.repository.DocumentsRepository
import com.iguana.domain.repository.LoginRepository
import com.iguana.domain.repository.RecentFileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAnnotationRepository(
        annotationRepositoryImpl: AnnotationRepositoryImpl
    ): AnnotationRepository

    @Binds
    @Singleton
    abstract fun bindDocumentsRepository(
        documentsRepositoryImpl: DocumentsRepositoryImpl
    ): DocumentsRepository

    @Binds
    @Singleton
    abstract fun bindRecentFileRepository(
        recentFileRepositoryImpl: RecentFileRepositoryImpl
    ): RecentFileRepository

    @Binds
    @Singleton
    abstract fun bindLoginRepository(
        loginRepositoryImpl: LoginRepositoryImpl
    ): LoginRepository
}