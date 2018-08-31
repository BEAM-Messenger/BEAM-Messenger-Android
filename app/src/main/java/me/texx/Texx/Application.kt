package me.texx.Texx

import android.app.Application
import daio.io.dresscode.DressCode
import daio.io.dresscode.declareDressCode

/**
 * Main Application class mainly for handling themes
 */
class Application : Application() {
    /**
     * Set initial configuration
     */
    override fun onCreate() {
        super.onCreate()

        declareDressCode(this,
                DressCode("dark", R.style.AppTheme_Dark),
                DressCode("light", R.style.AppTheme))
    }
}