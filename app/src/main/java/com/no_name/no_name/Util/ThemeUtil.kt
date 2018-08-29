package com.no_name.no_name.Util

import android.content.Context
import com.afollestad.aesthetic.Aesthetic
import com.madapps.prefrences.EasyPrefrences
import com.no_name.no_name.R

class ThemeUtil(context: Context) {
    private val sharedPrefs = EasyPrefrences(context)

    private fun isDarkTheme(): Boolean {
        val darkTheme: Boolean? = sharedPrefs.getBoolean("dark_theme_switch")
        darkTheme?.let {
            return darkTheme
        } ?: run {
            return false
        }
    }

    fun setActivityTheme(ActionBar: Boolean) {
        val theme: Int = if (!ActionBar) {
            if (isDarkTheme()) R.style.AppTheme_Dark_NoActionBar else R.style.AppTheme_NoActionBar
        } else
            if (isDarkTheme()) R.style.AppTheme_Dark else R.style.AppTheme

        Aesthetic.config {
            activityTheme(R.style.AppTheme_NoActionBar)
            isDark(isDarkTheme())
        }
    }
}