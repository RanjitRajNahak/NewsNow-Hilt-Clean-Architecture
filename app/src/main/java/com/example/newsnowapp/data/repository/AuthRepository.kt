package com.example.newsnowapp.data.repository

import com.example.app1.data.local.User
import com.example.app1.data.local.UserDao
import com.example.app1.ui.sessionLogin.SessionManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) {
    // Session state flow
    val isLoggedIn: Flow<Boolean> = sessionManager.isLoggedIn

    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }

    suspend fun registerUser(user: User) {
        userDao.registerUser(user)
    }

    suspend fun loginUser(email: String) {
        sessionManager.saveSession(email)
    }

    suspend fun logoutUser() {
        sessionManager.clearSession()
    }
}