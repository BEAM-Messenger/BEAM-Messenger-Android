package com.no_name.no_name

import android.app.Application
import daio.io.dresscode.DressCode
import daio.io.dresscode.declareDressCode

class Application : Application() {

    override fun onCreate() {
        super.onCreate()

        declareDressCode(this,
                DressCode("dark", R.style.AppTheme_Dark),
                DressCode("light", R.style.AppTheme))
    }
}