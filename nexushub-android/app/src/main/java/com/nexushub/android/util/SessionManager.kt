package com.nexushub.android.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "nexushub_session")

class SessionManager(private val context: Context) {

    companion object {
        private val KEY_TOKEN   = stringPreferencesKey("auth_token")
        private val KEY_USER_ID = longPreferencesKey("user_id")
        private val KEY_NAME    = stringPreferencesKey("user_name")
        private val KEY_EMAIL   = stringPreferencesKey("user_email")
    }

    val token: Flow<String?> = context.dataStore.data.map { it[KEY_TOKEN] }
    val userId: Flow<Long?>  = context.dataStore.data.map { it[KEY_USER_ID] }
    val userName: Flow<String?> = context.dataStore.data.map { it[KEY_NAME] }

    suspend fun saveSession(token: String, userId: Long, name: String, email: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TOKEN]   = token
            prefs[KEY_USER_ID] = userId
            prefs[KEY_NAME]    = name
            prefs[KEY_EMAIL]   = email
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        !prefs[KEY_TOKEN].isNullOrEmpty()
    }
}
