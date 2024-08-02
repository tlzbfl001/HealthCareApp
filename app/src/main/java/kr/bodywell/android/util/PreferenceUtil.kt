package kr.bodywell.android.util

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    fun setUserId(key: String, int: Int) {
        prefs.edit().putInt(key, int).apply()
    }

    fun getUserId(): Int {
        return prefs.getInt("userId", 0)
    }

    fun setMacId(key: String, str: String) {
        prefs.edit().putString(key, str).apply()
    }

    fun getMacId(): String {
        return prefs.getString(BluetoothUtil.MAC, "")!!
    }

    fun removePrefs() {
        prefs.edit().clear().apply()
    }
}