package me.texx.Texx

import android.os.Bundle
import android.os.StrictMode
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.TextView
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpGet
import daio.io.dresscode.dressCodeName
import daio.io.dresscode.matchDressCode
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import me.texx.Texx.util.ThemeUtil.getThemeName
import org.jetbrains.anko.alert
import org.jetbrains.anko.longToast
import org.jetbrains.anko.startActivity
import org.json.JSONArray
import org.json.JSONObject

/**
 * Main activity aka home screen of app
 */
class MainActivity : AppCompatActivity() {
    /**
     * Set initial configuration
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        matchDressCode()
        super.onCreate(savedInstanceState)
        dressCodeName = getThemeName(this)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        intentExtraActions()

        displayPosts()

        fab.setOnClickListener { _ ->
            startActivity<CameraActivity>()
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
            R.id.action_settings -> {
                startActivity<SettingsActivity>()
                true
            }
            R.id.action_bug_report -> {
                startActivity<BugReportActivity>()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun intentExtraActions() {
        if (intent.getBooleanExtra("serverDown", false)) {
            alert("We are sorry, but our servers do not seem to be working at the moment. Please wait a few minutes before you try again.", "Sorry") {
                isCancelable = false
                positiveButton("Okay") {
                    finishAndRemoveTask()
                    System.exit(0)
                }
            }.show()
        }

        if (intent.getBooleanExtra("notConnected", false))
            longToast("No internet connection!")
    }

    private fun getPosts(): JSONObject? {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val (_, _, result) = "/posts".httpGet().responseJson()
        val (data, error) = result
        return if (error == null) {
            data?.obj()
        } else {
            longToast("Error fetching posts!")
            null
        }
    }

    private fun displayPosts() {
        val data = getPosts()
        val postsArray = data?.get("posts") as JSONArray?

        postsArray?.let {
            for (i in 0 until postsArray.length()) {
                val postObject = postsArray.get(i) as JSONObject
                val postType = (postObject.get("post_type") as JSONObject).get("type")

                when (postType) {
                    "Text" -> {
                        val text: String = (postObject.get("post") as JSONObject).get("text").toString()
                        val padding = calculatePadding()
                        val textView = TextView(this)
                        textView.text = text
                        textView.setPadding(padding, padding, padding, padding)
                        textView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                        post_list.addView(textView)
                    }
                    "Media" -> {

                    }
                }
            }
        }
    }

    private fun calculatePadding(): Int {
        val paddingDp = 16
        val screenDensity = this.resources.displayMetrics.density
        return (paddingDp * screenDensity).toInt()
    }
}