package com.makebodywell.bodywell.util

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    fun setPrefs(key: String, int: Int) {
        prefs.edit().putInt(key, int).apply()
    }

    fun getPrefs(key: String, defValue: String): String {
        return prefs.getString(key, defValue).toString()
    }

    fun getId(): Int {
        return prefs.getInt("userId", 0)
    }

    fun removePrefs(key: String) {
        prefs.edit().remove(key)
        prefs.edit().clear()
        prefs.edit().apply()
    }
}