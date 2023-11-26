package com.example.todo

import android.app.Application
import com.example.todo.data.MySharedPref

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        MySharedPref.init(this)
    }
}
