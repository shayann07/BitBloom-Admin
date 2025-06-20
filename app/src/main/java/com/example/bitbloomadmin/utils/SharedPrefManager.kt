package com.example.bitbloomadmin.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson


class SharedPrefManager(context: Context) {

    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(Constants.PREFERENCE, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPref.edit()
    private val gson = Gson()

    fun setLoggedIn(isLoggedIn: Boolean) {
        editor.putBoolean("isLoggedIn", isLoggedIn).apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPref.getBoolean("isLoggedIn", false)
    }
}
