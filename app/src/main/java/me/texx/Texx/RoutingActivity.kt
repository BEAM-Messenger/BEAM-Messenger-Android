package me.texx.Texx

import android.os.Bundle
import android.os.StrictMode
import android.support.v7.app.AppCompatActivity
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import com.madapps.prefrences.EasyPrefrences
import daio.io.dresscode.dressCodeName
import daio.io.dresscode.matchDressCode
import me.texx.Texx.util.ThemeUtil.getThemeName
import org.jetbrains.anko.*
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket


/**
 * Activity which will be run before any other to verify user and choose which activity
 * should be started next
 */
class RoutingActivity : AppCompatActivity() {
    private val serverAddress = "192.168.137.1"

    /**
     * Set initial configuration
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        matchDressCode()
        super.onCreate(savedInstanceState)
        dressCodeName = getThemeName(this)

        FuelManager.instance.basePath = "http://$serverAddress"

        if (EasyPrefrences(this@RoutingActivity).getBoolean("previously_started")) {
            alert("Logging you in.", "Loading...") {
                isCancelable = false
            }.show()
            verifyLogin()
        } else {
            EasyPrefrences(this@RoutingActivity).putBoolean("previously_started", true)
            startActivity<IntroActivity>()
        }

    }

    /**
     * Checks if client is connected to the internet by pinging google
     */
    private fun isConnected(): Boolean {
        return try {
            val policy = StrictMode.ThreadPolicy.Builder()
                    .permitAll().build()
            StrictMode.setThreadPolicy(policy)
            val timeoutMs = 1500
            val socket = Socket()
            val socketAddress = InetSocketAddress("8.8.8.8", 53)
            socket.connect(socketAddress, timeoutMs)
            socket.close()
            true
        } catch (e: IOException) {
            false
        }
    }

    private fun verifyLogin() {
        val accessToken: String? = SecureStorage(this@RoutingActivity).get("access_token")
        // synced function of fuel doesn't work here (#331) -> ugly workaround
        if (accessToken != null) {
            val userID = EasyPrefrences(this@RoutingActivity).getString("user_id")
            "/users/$userID".httpGet() // verify by making request to user api
                    .header("Authorization" to "Bearer $accessToken")
                    .responseJson { _, response, result ->
                        val (_, serverError) = result
                        when { // TODO: Cleaner task solution
                            response.httpStatusCode == 200 -> startActivity(intentFor<MainActivity>().newTask().clearTask().noAnimation().excludeFromRecents())
                            response.httpStatusCode == 401 -> startActivity(intentFor<LoginActivity>().newTask().clearTask().noAnimation().excludeFromRecents())
                            !isConnected() -> startActivity(intentFor<MainActivity>("notConnected" to true).newTask().clearTask().noAnimation().excludeFromRecents())
                            serverError != null -> startActivity(intentFor<MainActivity>("serverDown" to true).newTask().clearTask().noAnimation().excludeFromRecents())
                            else -> startActivity(intentFor<LoginActivity>().newTask().clearTask().noAnimation().excludeFromRecents())
                        }
                    }
        } else {
            startActivity<LoginActivity>()
        }
    }
}