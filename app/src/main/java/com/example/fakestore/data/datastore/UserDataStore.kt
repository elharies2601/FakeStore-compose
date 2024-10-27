package com.example.fakestore.data.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    companion object {
        private val AUTH_TOKEN = stringPreferencesKey("auth_token")
        private val IS_LOGIN = booleanPreferencesKey("is_login")
        private val USERNAME = stringPreferencesKey("username")
    }

    val authToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[AUTH_TOKEN]
    }

    val isLogin: Flow<Boolean?> = context.dataStore.data.map { preferences -> preferences[IS_LOGIN] }
    val username: Flow<String?> = context.dataStore.data.map { preferences -> preferences[USERNAME] }

    suspend fun saveUsername(username: String) {
        context.dataStore.edit { preferences ->
            preferences[USERNAME] = username
        }
    }

    suspend fun setLogin(isLogin: Boolean = false) {
        context.dataStore.edit {
            it[IS_LOGIN] = isLogin
        }
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[AUTH_TOKEN] = token
        }
    }

    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(AUTH_TOKEN)
            preferences.remove(IS_LOGIN)
        }
    }
}