package com.no_name.no_name

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import com.madapps.prefrences.EasyPrefrences
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import java.io.IOException

/**
 * Activity which will be run before any other to verify user and choose which activity
 * should be started next
 */
class InitialActivity : AppCompatActivity() {
    private val server_address = "192.168.0.102"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FuelManager.instance.basePath = "http://$server_address"
        alert("Logging you in.", "Loading...") {
            isCancelable = false
        }.show()
        verifyLogin()
    }

    @Throws(InterruptedException::class, IOException::class)
    fun isConnected(): Boolean {
        val command = "ping -c 1 google.com"
        return Runtime.getRuntime().exec(command).waitFor() == 0
    }

    private fun verifyLogin() {
        val accessToken: String? = SecureStorage(this@InitialActivity).get("access_token")
        // synced function of fuel doesn't work here (#331) -> ugly workaround
        if (accessToken != null) {
            val userID = EasyPrefrences(this@InitialActivity).getString("user_id")
            "/users/$userID".httpGet() // verify by making request to user api
                    .header("Authorization" to "Bearer $accessToken")
                    .responseJson { _, response, result ->
                        val (_, serverError) = result
                        when {
                            response.httpStatusCode == 200 -> startActivity<MainActivity>()
                            !isConnected() -> startActivity<MainActivity>("notConnected" to true)
                            serverError != null -> startActivity<MainActivity>("serverDown" to true)
                            else -> startActivity<LoginActivity>()
                        }
                    }
        } else {
            startActivity<LoginActivity>()
        }
    }
}