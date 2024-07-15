package kr.bodywell.health.util

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    fun setPrefs(key: String, int: Int) {
        prefs.edit().putInt(key, int).apply()
    }

    fun getId(): Int {
        return prefs.getInt("userId", 0)
    }

    fun removePrefs() {
        prefs.edit().clear().apply()
    }
}