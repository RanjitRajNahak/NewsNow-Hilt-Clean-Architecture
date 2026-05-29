package com.example.newsnowapp.di

import android.content.Context
import com.example.app1.data.local.UserDao
import com.example.app1.data.local.UserDatabase
import com.example.app1.ui.sessionLogin.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // 1. Tell Hilt how to provide SessionManager using the application context
    @Provides
    @Singleton
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManager(context)
    }

    // 2. Tell Hilt how to build your room user database instance
    @Provides
    @Singleton
    fun provideUserDatabase(@ApplicationContext context: Context): UserDatabase {
        return UserDatabase.getDatabase(context)
    }

    // 3. Extract and provide the user DAO dependency required by AuthRepository
    @Provides
    @Singleton
    fun provideUserDao(database: UserDatabase): UserDao {
        return database.userDao()
    }
}