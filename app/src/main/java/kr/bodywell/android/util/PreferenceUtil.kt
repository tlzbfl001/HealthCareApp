package kr.bodywell.android.util

import android.content.Context
import android.content.SharedPreferences
import kr.bodywell.android.model.Constants.DEVICE
import kr.bodywell.android.model.Constants.PREFERENCE

class PreferenceUtil(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    fun setUserId(key: String, int: Int) {
        prefs.edit().putInt(key, int).apply()
    }

    fun getUserId(): Int {
        return prefs.getInt(PREFERENCE, 0)
    }

    fun setDevice(hashSet: HashSet<String>) {
        prefs.edit().putStringSet(DEVICE, hashSet).apply()
    }

    fun getDevice(): Set<String> {
        return prefs.getStringSet(DEVICE, null)!!
    }

    fun removePrefs() {
        prefs.edit().clear().apply()
    }
}