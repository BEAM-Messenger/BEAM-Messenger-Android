package me.texx.Texx

import android.os.Bundle
import android.os.StrictMode
import android.support.v7.app.AppCompatActivity
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpGet
import com.madapps.prefrences.EasyPrefrences
import daio.io.dresscode.dressCodeName
import daio.io.dresscode.matchDressCode
import kotlinx.android.synthetic.main.activity_bug_report.*
import me.texx.Texx.util.ThemeUtil
import org.eclipse.egit.github.core.Issue
import org.eclipse.egit.github.core.client.GitHubClient
import org.eclipse.egit.github.core.service.IssueService
import org.jetbrains.anko.longToast
import org.json.JSONObject
import java.io.IOException


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
        val issueTitle = edit_title.text.toString()
        val username = getVerifiedUsername()
        val issueDescription = "${edit_description.text}\n\n$debugInformation\n\nBy ${username.toString()}"
        val issue = Issue().setTitle(issueTitle).setBody(issueDescription)

        val accessToken = "7ac99190d8e9319eaa6ccc61a23d44419c59bb0c" // TODO: Secure GitHub token
        val service = IssueService(GitHubClient().setOAuth2Token(accessToken))
        try { // TODO: add loading animation in bug reporter
            service.createIssue("texxme", "Texx-Android", issue)
            longToast("Issue submitted")
        } catch (e: IOException) {
            longToast("Something went wrong :(")
        }
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
