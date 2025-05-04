package com.officialsunil.pdpapplication.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.officialsunil.pdpapplication.viewui.CameraActivity
import com.officialsunil.pdpapplication.viewui.DiagnosesListActivity
import com.officialsunil.pdpapplication.viewui.HomeActivity
import com.officialsunil.pdpapplication.viewui.StatisticsActivity

object NavigationUtils {
    fun Navigate(context: Context, destination: String) {
        val intent = when (destination) {
            "home" -> Intent(context, HomeActivity::class.java)
            "statistics" -> Intent(context, StatisticsActivity::class.java)
            "camera" -> Intent(context, CameraActivity::class.java)
            "diagnosesList" -> Intent(context, DiagnosesListActivity::class.java)
            else -> Intent(context, DiagnosesListActivity::class.java)
        }
        context.startActivity(intent)
        if (destination == "home" && context is Activity) context.finish()
    }
}
