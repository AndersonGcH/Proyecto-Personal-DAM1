package com.example.mimonto.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("MiMontoPrefs", Context.MODE_PRIVATE)

    companion object {
        const val USER_ID = "user_id"
    }

    fun saveAuthToken(id: Int) {
        val editor = prefs.edit()
        editor.putInt(USER_ID, id)
        editor.apply()
    }

    fun getUserId(): Int {
        return prefs.getInt(USER_ID, -1)
    }
}