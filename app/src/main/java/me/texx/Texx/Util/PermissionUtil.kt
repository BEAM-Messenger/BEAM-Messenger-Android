package me.texx.Texx.util

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.finishAffinity
import android.support.v4.content.ContextCompat
import org.jetbrains.anko.alert


/**
 * Object with function for asking for permissions
 */
object PermissionUtil {
    /**
     * Asks for specified permission and starts actions depending on users choose
     */
    fun askForPermission(permissionString: String, activity: Activity) {
        if (ContextCompat.checkSelfPermission(activity, permissionString) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionString)) {
                activity.alert(getPermissionText(permissionString), "${getPermissionName(permissionString, activity)}-Permission is missing") {
                    isCancelable = false
                    negativeButton("Never!") {
                        finishAffinity(activity)
                    }
                    positiveButton("Okay") {
                        ActivityCompat.requestPermissions(activity, arrayOf(permissionString), 1)
                    }
                }.show()
            } else {
                ActivityCompat.requestPermissions(activity, arrayOf(permissionString), 1)
            }
        }
    }

    /**
     * Returns true if permission is granted
     */
    fun permissionGranted(permissionString: String, activity: Activity): Boolean {
        return ContextCompat.checkSelfPermission(activity, permissionString) == PackageManager.PERMISSION_GRANTED
    }

    private fun getPermissionName(permissionString: String, activity: Activity): String {
        val packageManager = activity.packageManager
        val permissionInfo = packageManager.getPermissionInfo(permissionString, 0)
        val permissionGroupInfo = packageManager.getPermissionGroupInfo(permissionInfo.group, 0)
        return permissionGroupInfo.loadLabel(packageManager).toString()
    }

    /**
     * Gets the text for permission requests
     */
    private fun getPermissionText(permissionString: String): String {
        return when (permissionString) {
            ACCESS_FINE_LOCATION -> "Optional permission to classify your image by location - deny if you don't want to share the photo's location."
            WRITE_EXTERNAL_STORAGE -> "We need this permission in order to save your image on you device."
            else -> "Please allow if you want to use the apps whole potential."
        }
    }
}