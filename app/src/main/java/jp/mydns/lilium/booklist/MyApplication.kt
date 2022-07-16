package jp.mydns.lilium.booklist

import android.app.Application

class MyApplication : Application() {
    lateinit var myPreferences: MyPreferences

    override fun onCreate() {
        super.onCreate()
        myPreferences = MyPreferences(this)
    }
}