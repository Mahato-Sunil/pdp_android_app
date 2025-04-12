package com.officialsunil.pdpapplication.utils

import android.app.Activity.RESULT_OK
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class GoogleAccountPickerContract : ActivityResultContract<Intent, Boolean>() {
    override fun createIntent(context: Context, input: Intent): Intent = input
    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return resultCode == Activity.RESULT_OK
    }
}
//
//// function to initiate google login
//// launcher for the goolge authentication
//private val googleSignInLauncher =
//    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == RESULT_OK) Log.d(
//            "GoogleAuth", "Account picker returned successfully."
//        )
//        else Log.w("GoogleAuth", "Account picker was canceled.")
//    }
//
//private fun initGoogleLogin() {
//    GoogleAuthUtils.initiateGoogleSignin(
//        context = this,
//        scope = lifecycleScope,
//        launcher = googleSignInLauncher,
//        googleLogin = {
//            runOnUiThread {
//                navigateToHome()
//                Toast.makeText(this, "Authentication Successfull", Toast.LENGTH_SHORT).show()
//            }
//        })
//}