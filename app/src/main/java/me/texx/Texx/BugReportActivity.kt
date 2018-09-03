package me.texx.Texx

import android.os.Bundle
import android.os.StrictMode
import android.support.v7.app.AppCompatActivity
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.madapps.prefrences.EasyPrefrences
import daio.io.dresscode.dressCodeName
import daio.io.dresscode.matchDressCode
import kotlinx.android.synthetic.main.activity_bug_report.*
import me.texx.Texx.Util.SecureStorage
import me.texx.Texx.util.ThemeUtil
import org.jetbrains.anko.longToast
import org.json.JSONObject

/**
 * Activity to report bugs/issues directly on Github
 */
class BugReportActivity : AppCompatActivity() {
    /**
     * Set initial configuration
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        matchDressCode()
        super.onCreate(savedInstanceState)
        dressCodeName = ThemeUtil.getThemeName(this)
        setContentView(R.layout.activity_bug_report)

        val debugInformation = getDebugInformation()
        debug_text.text = debugInformation

        fab.setOnClickListener {
            submitToGithub(debugInformation)
        }
    }

    private fun submitToGithub(debugInformation: String) {
        val accessToken = "f4f048af0e3f2d36e78b98452d3398fb8c051088" // TODO: Secure GitHub token

        val issueJson = JSONObject()
        val issueTitle = edit_title.text.toString()
        val issueDescription = edit_description.text.toString()
        val username = getVerifiedUsername()
        issueJson.put("title", issueTitle)
        issueJson.put("body", "$issueDescription\n$debugInformation\n\nBy ${username.toString()}")

        // clear configuration for github api // TODO: Cleaner solution for fuel configuration
        FuelManager.instance.baseHeaders = null
        FuelManager.instance.basePath = null // TODO: Set IP as public variable

        username?.let {
            val policy = StrictMode.ThreadPolicy.Builder()
                    .permitAll().build()
            StrictMode.setThreadPolicy(policy)
            val (_, response, res) = "https://api.github.com/repos/texxme/Texx-Android/issues/".httpPost() // verify by making request to user api // TODO: More secure way of verifying
                    .header("Authorization" to "token: $accessToken")
                    .body(issueJson.toString())
                    .responseString()
            longToast(if (response.httpStatusCode == 201) "Issue submitted" else "Something went wrong :(")
        } ?: run {
            longToast("Error verifying your account.")
        }

        // set configuration again
        val texxAccessToken: String? = SecureStorage(this@BugReportActivity).get("access_token")
        if (texxAccessToken != null)
            FuelManager.instance.baseHeaders = mapOf("Authorization" to "Bearer $accessToken")
        FuelManager.instance.basePath = "http://192.168.137.1" // TODO: Set IP as public variable
    }

    private fun getVerifiedUsername(): Any? {
        val userID = EasyPrefrences(this@BugReportActivity).getString("user_id")
        val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val (_, _, result) = "/users/$userID".httpGet() // verify by making request to user api // TODO: More secure way of verifying
                .responseJson()
        return (result.get().obj().get("user") as JSONObject).get("name")
    }

    private fun getDebugInformation(): String {
        return "Debug Information:" +
                "\n OS Version: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")" +
                "\n OS API Level: " + android.os.Build.VERSION.SDK_INT +
                "\n Device: " + android.os.Build.DEVICE +
                "\n Model (and Product): " + android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")" +
                "\n RELEASE: " + android.os.Build.VERSION.RELEASE +
                "\n BRAND: " + android.os.Build.BRAND +
                "\n DISPLAY: " + android.os.Build.DISPLAY +
                "\n CPU_ABI: " + android.os.Build.CPU_ABI +
                "\n CPU_ABI2: " + android.os.Build.CPU_ABI2 +
                "\n HARDWARE: " + android.os.Build.HARDWARE +
                "\n MANUFACTURER: " + android.os.Build.MANUFACTURER
    }
}
