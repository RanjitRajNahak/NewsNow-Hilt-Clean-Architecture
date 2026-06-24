package com.example.newsnowapp.data.repository

import com.example.newsnowapp.data.local.user.User
import com.example.newsnowapp.data.local.user.UserDao
import com.example.newsnowapp.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao
): AuthRepository {

    override suspend fun getUserByEmail(email:String): User? = userDao.getUserByEmail(email)

    override suspend fun registerUser(user: User) = userDao.registerUser(user)
}