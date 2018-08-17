package com.no_name.no_name

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import com.madapps.prefrences.EasyPrefrences

/**
 * Activity which will be run before any other to verify user and choose which activity
 * should be started next
 */
class InitialActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FuelManager.instance.basePath = "http://192.168.0.59"
        verifyLogin()
    }

    private fun verifyLogin() {
        val accessToken: String? = SecureStorage(this@InitialActivity).get("access_token")
        var intent = Intent(this@InitialActivity, LoginActivity::class.java)

        if (accessToken != null) { // TODO: Check if user has internet connection, if not -> show MA without verification
            val userID = EasyPrefrences(this@InitialActivity).getString("user_id")
            "/users/$userID".httpGet() // synced function of fuel doesn't work here (#331) -> ugly workaround
                    .header("Authorization" to "Bearer $accessToken")
                    .responseJson { _, _, result ->
                        val (_, error) = result
                        if (error == null) intent = Intent(this@InitialActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
        } else {
            startActivity(intent)
        }
    }
}