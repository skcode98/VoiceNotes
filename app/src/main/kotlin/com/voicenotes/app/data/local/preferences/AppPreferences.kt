package com.voicenotes.app.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

class AppPreferences(private val context: Context) {
    companion object {
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val AUTH_TOKEN = stringPreferencesKey("auth_token")
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val THEME_MODE = stringPreferencesKey("theme_mode")
        private val TRANSCRIPTION_LANGUAGE = stringPreferencesKey("transcription_language")
        private val LAST_SYNC_TIME = longPreferencesKey("last_sync_time")
    }

    val userId: Flow<String?> = context.dataStore.data.map { it[USER_ID] }
    val userEmail: Flow<String?> = context.dataStore.data.map { it[USER_EMAIL] }
    val authToken: Flow<String?> = context.dataStore.data.map { it[AUTH_TOKEN] }
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[IS_LOGGED_IN] ?: false }
    val themeMode: Flow<String> = context.dataStore.data.map { it[THEME_MODE] ?: "system" }
    val transcriptionLanguage: Flow<String> = context.dataStore.data.map { it[TRANSCRIPTION_LANGUAGE] ?: "en" }
    val lastSyncTime: Flow<Long> = context.dataStore.data.map { it[LAST_SYNC_TIME] ?: 0L }

    suspend fun setUserId(userId: String) {
        context.dataStore.edit { it[USER_ID] = userId }
    }

    suspend fun setUserEmail(email: String) {
        context.dataStore.edit { it[USER_EMAIL] = email }
    }

    suspend fun setAuthToken(token: String) {
        context.dataStore.edit { it[AUTH_TOKEN] = token }
    }

    suspend fun setIsLoggedIn(isLoggedIn: Boolean) {
        context.dataStore.edit { it[IS_LOGGED_IN] = isLoggedIn }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { it[THEME_MODE] = mode }
    }

    suspend fun setTranscriptionLanguage(language: String) {
        context.dataStore.edit { it[TRANSCRIPTION_LANGUAGE] = language }
    }

    suspend fun setLastSyncTime(time: Long) {
        context.dataStore.edit { it[LAST_SYNC_TIME] = time }
    }

    suspend fun clearUserData() {
        context.dataStore.edit {
            it.remove(USER_ID)
            it.remove(USER_EMAIL)
            it.remove(AUTH_TOKEN)
            it[IS_LOGGED_IN] = false
        }
    }
}
