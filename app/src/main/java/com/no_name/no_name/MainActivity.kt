package com.no_name.no_name

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.afollestad.aesthetic.AestheticActivity
import com.no_name.no_name.Util.ThemeUtil
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.longToast
import org.jetbrains.anko.startActivity

/**
 * Main activity aka home screen of app
 */
class MainActivity : AestheticActivity() {
    /**
     * Set initial configuration
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (ThemeUtil(this)::setActivityTheme)(false)

        if (intent.getBooleanExtra("serverDown", false)) {
            alert("We are sorry, but our servers do not seem to be working at the moment. Please wait a few minutes before you try again.", "Sorry") {
                positiveButton("Okay") {
                    finishAffinity() // TODO: Loading activity will somehow still be opened after close
                    System.exit(0)
                }
            }.show()
        }

        if (intent.getBooleanExtra("notConnected", false))
            longToast("No internet connection!")

        fab.setOnClickListener { view ->
            startActivity<SettingsActivity>()
        }
    }


    /**
     * Inflate the [menu]; this adds items to the action bar if it is present
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /**
     * Handling action bar [item] clicks
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
