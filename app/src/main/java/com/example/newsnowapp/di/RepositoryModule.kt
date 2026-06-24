package com.example.newsnowapp.di

import com.example.newsnowapp.data.repository.AuthRepositoryImpl
import com.example.newsnowapp.data.repository.NewsRepositoryImpl
import com.example.newsnowapp.domain.repository.AuthRepository
import com.example.newsnowapp.domain.repository.NewsRepository
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
    abstract fun bindNewsRepository(
        newsRepositoryImpl: NewsRepositoryImpl
    ): NewsRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
}