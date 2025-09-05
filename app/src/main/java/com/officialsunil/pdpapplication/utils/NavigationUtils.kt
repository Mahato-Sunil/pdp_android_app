package com.officialsunil.pdpapplication.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.officialsunil.pdpapplication.admin.screen.AdminAddDisease
import com.officialsunil.pdpapplication.admin.screen.AdminCasesScreen
import com.officialsunil.pdpapplication.admin.screen.AdminHome
import com.officialsunil.pdpapplication.admin.screen.AdminLoginScreen
import com.officialsunil.pdpapplication.admin.screen.AdminSettingScreen
import com.officialsunil.pdpapplication.viewui.AboutActivity
import com.officialsunil.pdpapplication.viewui.AccountCenterActivity
import com.officialsunil.pdpapplication.viewui.CameraActivity
import com.officialsunil.pdpapplication.viewui.DiagnosesListActivity
import com.officialsunil.pdpapplication.viewui.DiseaseAnalysis
import com.officialsunil.pdpapplication.viewui.GoogleSignInRationale
import com.officialsunil.pdpapplication.viewui.HomeActivity
import com.officialsunil.pdpapplication.viewui.MainActivity
import com.officialsunil.pdpapplication.viewui.PredictionActivity
import com.officialsunil.pdpapplication.viewui.StatisticsActivity

object NavigationUtils {
    fun navigate(
        context: Context, destination: String, finish: Boolean = false, data: String = ""
    ) {
        val intent = when (destination) {
            "about" -> Intent(context, AboutActivity::class.java)
            "register" -> Intent(context, MainActivity::class.java)
            "home" -> Intent(context, HomeActivity::class.java)
            "statistics" -> Intent(context, StatisticsActivity::class.java)
            "camera" -> Intent(context, CameraActivity::class.java)
            "diagnosesList" -> Intent(context, DiagnosesListActivity::class.java)
            "accountCenter" -> Intent(context, AccountCenterActivity::class.java)
            "prediction" -> Intent(context, PredictionActivity::class.java)
            "signinRationale" -> Intent(context, GoogleSignInRationale::class.java)
            "analysis" -> Intent(context, DiseaseAnalysis::class.java)

            /* * Admin Screens * */

            "adminLogin" -> Intent(context, AdminLoginScreen::class.java)
            "adminHome" -> Intent(context, AdminHome::class.java)
            "adminCase" -> Intent(context, AdminCasesScreen::class.java)
            "adminSettings" -> Intent(context, AdminSettingScreen::class.java)
            "addDisease" -> Intent(context, AdminAddDisease::class.java)

            else -> Intent(context, HomeActivity::class.java)
        }
        if (data.isNotEmpty()) {
            intent.putExtra("diseaseId", data)
        }

        context.startActivity(intent)


        if (destination == "home" && context is Activity) context.finish()
        if (finish && context is Activity) context.finish()
    }
}
