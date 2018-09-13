package me.texx.Texx.util

import android.content.Context
import com.madapps.prefrences.EasyPrefrences
import me.texx.Texx.util.ThemeUtil.isDarkTheme

/**
 * Get the name of the theme depending on [actionBar] and [isDarkTheme]
 */
object ThemeUtil {
    /**
     * Checks if the theme saved in sharedPreferences is dark/light
     */
    private fun isDarkTheme(context: Context): Boolean {
        val sharedPrefs = EasyPrefrences(context)
        val darkTheme: Boolean? = sharedPrefs.getBoolean("dark_theme_switch")
        darkTheme?.let {
            return darkTheme
        } ?: run {
            return false
        }
    }

    /**
     * Get the name of the theme depending on [actionBar] and [isDarkTheme]
     */
    fun getThemeName(context: Context): String {
        return if (isDarkTheme(context)) {
            "dark"
        } else {
            "light"
        }
    }
}