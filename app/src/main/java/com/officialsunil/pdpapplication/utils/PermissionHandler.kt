package com.officialsunil.pdpapplication.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.content.ContextCompat

class PermissionHandler {

    // for camera x
    companion object {
        // function to check the camera permisson
        fun checkCameraPermission(context: Context): Boolean {
            return CAMERAX_PERMISSION.all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
        }

        fun requestCameraPermission(activity: Activity, context: Context) {
            // check if the camera permission is granted
            if (!checkCameraPermission(context)) activity.requestPermissions(CAMERAX_PERMISSION, 1)
            else Toast.makeText(context, "Camera Permission Granted", Toast.LENGTH_LONG).show()
        }

        // define the companion object for the camera x
        // companion objects
        private val CAMERAX_PERMISSION = arrayOf(
            android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO
        )
    }
}
