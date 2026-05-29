package com.example.app1.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.app1.data.local.User

import com.example.app1.data.local.UserDatabase
import com.example.app1.ui.sessionLogin.SessionManager
import com.example.newsnowapp.data.repository.AuthRepository
import kotlinx.coroutines.launch
class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = UserDatabase.getDatabase(application).userDao()
    private val sessionManager = SessionManager(application)
    private val repository = AuthRepository(userDao, sessionManager)

    var isEmailAlreadyRegistered by mutableStateOf(false)
        private set

    val isLoggedIn = repository.isLoggedIn // Read straight from repo

    fun checkIfEmailExists(email: String) {
        viewModelScope.launch {
            val user = repository.getUserByEmail(email)
            isEmailAlreadyRegistered = user != null
        }
    }

    fun signUp(user: User, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.registerUser(user)
            onSuccess()
        }
    }

    fun login(email: String, pass: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val user = repository.getUserByEmail(email)
            when {
                user == null -> onResult(false, "User Not Registered")
                user.password != pass -> onResult(false, "Incorrect Password")
                else -> {
                    repository.loginUser(email)
                    onResult(true, "Success")
                }
            }
        }
    }

    // Add this inside your AuthViewModel class
    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            repository.logoutUser()
            onLogoutComplete() // Callback to trigger UI navigation back to Login
        }
    }
}
