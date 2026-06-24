package com.example.newsnowapp.presentation.auth

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsnowapp.data.local.user.User
import com.example.newsnowapp.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    @ApplicationContext private val context: Context
): ViewModel() {
    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    companion object{
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    var isEmailAlreadyRegistered by mutableStateOf(false)

    fun checkLoginStatus(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun checkIfEmailExists(email: String) {
        viewModelScope.launch {
            val user = repository.getUserByEmail(email)
            isEmailAlreadyRegistered = user != null
        }
    }

    fun signUp(user: User, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            repository.registerUser(user)
            onResult(true, "Account created successfully!")
        }
    }

    fun login(email: String, pass: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val user = repository.getUserByEmail(email)
            when {
                user == null -> onResult(false, "User Not Registered")
                user.password != pass -> onResult(false, "Incorrect Credential")
                else -> {
                    setLoggedIn(true)
                    onResult(true, "Success")
                }
            }
        }
    }

    fun logout() {
        setLoggedIn(false)
    }

    fun setLoggedIn(boolean: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, boolean).apply()
    }

}