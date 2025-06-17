package com.example.bitbloomadmin.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.bitbloomadmin.models.NotificationItem
import com.example.bitbloomadmin.models.UserModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


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

    fun saveUserEmail(email: String) {
        editor.putString("userEmail", email)
        editor.apply()
    }
    fun getEmail(): String? {
        return sharedPref.getString("userEmail", null)
    }

    fun getId(): String? {
        return sharedPref.getString("userId", null)
    }





    fun saveUsers(list: List<UserModel>) {
        val json = gson.toJson(list)
        editor.putString("users_list", json)
        editor.apply()
    }




}
