package com.makebodywell.bodywell.util

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    fun setPrefs(key: String, int: Int) {
        prefs.edit().putInt(key, int).apply()
    }

    fun getId(): Int {
        return prefs.getInt("userId", -1)
    }

    fun removePrefs(key: String) {
        prefs.edit().remove(key).apply()
    }
}