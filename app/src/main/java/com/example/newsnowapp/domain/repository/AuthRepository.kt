package com.example.newsnowapp.domain.repository

import com.example.newsnowapp.data.local.user.User

interface AuthRepository {
    suspend fun getUserByEmail(email: String): User?
    suspend fun registerUser(user: User)
}