package com.example.fitfinder.util

import android.content.Context

object SharedPreferencesUtil {
    private const val SHARED_PREF_NAME = "FitFinderPreferences"
    private const val KEY_USER_ID = "keyUserId"

    fun saveUserId(context: Context, userId: String) {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(KEY_USER_ID, userId).apply()
    }

    fun getUserId(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_USER_ID, null)
    }
}
