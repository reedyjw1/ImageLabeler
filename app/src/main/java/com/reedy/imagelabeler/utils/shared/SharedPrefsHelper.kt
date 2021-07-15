package com.reedy.imagelabeler.utils.shared

import android.content.Context
import com.reedy.imagelabeler.R

class SharedPrefsHelper(val context: Context): ISharedPrefsHelper {

    companion object {
        private const val PREFS = "image_labeler_prefs"
    }

    private val sharedPrefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    override fun getSharedPrefs(key: SharedPrefsKeys): String? {
        return sharedPrefs.getString(key.value, "")
    }

    override fun saveToSharedPrefs(key: SharedPrefsKeys, value: String) {
        with(sharedPrefs.edit()) {
            putString(key.value, value)
            commit()
        }
    }
}

enum class SharedPrefsKeys(val value: String) {
    DIR_URI("pwd_uri")
}