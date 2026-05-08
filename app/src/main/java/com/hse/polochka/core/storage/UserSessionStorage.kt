package com.hse.polochka.core.storage

import android.content.Context

class UserSessionStorage(context: Context) {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        preferences.edit()
            .putString(KEY_TOKEN, token)
            .apply()
    }

    fun getToken(): String? = preferences.getString(KEY_TOKEN, null)

    fun clear() {
        preferences.edit().clear().apply()
    }

    companion object {
        private const val PREFERENCES_NAME = "user_session"
        private const val KEY_TOKEN = "token"
    }
}
