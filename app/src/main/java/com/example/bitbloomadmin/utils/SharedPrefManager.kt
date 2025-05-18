package com.bitbloom.bitbloomadmin.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.bitbloomadmin.models.UserModel
import com.example.bitbloomadmin.utils.Constants
import com.google.gson.Gson


class SharedPrefManager(context: Context) {

    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(Constants.PREFERENCE, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPref.edit()
    private val gson = Gson()

    fun saveId(id: String) {
        editor.putString("userId", id)
        editor.apply()
    }
    fun saveUserName(name: String) {
        editor.putString("userName", name)
        editor.apply()
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
