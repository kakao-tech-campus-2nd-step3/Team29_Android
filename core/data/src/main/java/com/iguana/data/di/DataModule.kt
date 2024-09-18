package com.iguana.data.di

//import com.iguana.data.repository.DefaultUserRepository
//import com.iguana.data.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    // NOTE : 이런식으로 바인딩하는 모듈 (안쓰이면 삭제 가능)
//    @Binds
//    fun bindsUserRepository(
//        userRepository: DefaultUserRepository
//    ): UserRepository
}