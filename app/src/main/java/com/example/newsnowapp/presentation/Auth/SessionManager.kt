package com.example.app1.ui.sessionLogin


import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("user_session")

class SessionManager(private val context: Context) {
    companion object {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_EMAIL = stringPreferencesKey("user_email")
    }

    // Save session
    suspend fun saveSession(email: String) {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = true
            prefs[USER_EMAIL] = email
        }
    }

    // Clear session (for Logout)
    // Inside com.example.app1.ui.sessionLogin.SessionManager
    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.clear() // 🧠 This completely wipes the "user_session" DataStore file!
        }
    }

    // Read session state
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[IS_LOGGED_IN] ?: false
    }
}