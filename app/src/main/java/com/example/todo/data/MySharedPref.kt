package com.example.todo.data

import android.content.Context
import android.content.SharedPreferences

object MySharedPref {

    private const val NAME = "MyPreferences"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, MODE)
    }

  /*  fun putString(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String): String {
        return preferences.getString(key, defaultValue) ?: defaultValue
    }*/

    fun putBool(key:String,value:Boolean){
        preferences.edit().putBoolean(key,value).apply()
    }

    fun getBool(key:String,default:Boolean): Boolean{
        return preferences.getBoolean(key,default)
    }

}