package jp.mydns.lilium.booklist

import android.app.Application

class MyApplication : Application() {
    lateinit var myPreferences: PreferencesModel

    override fun onCreate() {
        super.onCreate()
        myPreferences = PreferencesModel(this)
    }
}