package com.reedy.imagelabeler.utils.shared

interface ISharedPrefsHelper {
    fun getSharedPrefs(key: SharedPrefsKeys): String?
    fun saveToSharedPrefs(key: SharedPrefsKeys, value: String)
}