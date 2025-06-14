package com.officialsunil.pdpapplication.utils.firebase

import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.credentials.*
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.officialsunil.pdpapplication.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

object GoogleAuthUtils {
    private const val TAG = "GoogleAuthUtils"

    fun initiateGoogleSignin(
        context: Context,
        scope: CoroutineScope,
        launcher: ActivityResultLauncher<Intent>?,
        googleLogin: () -> Unit
    ) {
        val credentialManager = CredentialManager.create(context)

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(getCredentialOptions(context))
            .build()

        scope.launch {
            try {
                val result = credentialManager.getCredential(context, request)
                handleGoogleSignin(result, googleLogin)
            } catch (_: NoCredentialException) {
                Log.w(TAG, "No credentials found, launching fallback")

                // Show user-friendly fallback message for Android 10 and below
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                    Toast.makeText(
                        context,
                        "Select a Google account manually to continue.",
                        Toast.LENGTH_LONG
                    ).show()
                }

                if (!isGooglePlayServicesAvailable(context)) {
                    Toast.makeText(
                        context,
                        "Google Play Services may be outdated. Please update it for better experience.",
                        Toast.LENGTH_LONG
                    ).show()
                }

                // Only launch "Add Account" intent if no Google accounts are available
                if (!hasGoogleAccounts(context)) {
                    launcher?.launch(getIntent())
                }
            } catch (e: GetCredentialException) {
                Log.e(TAG, "Credential error: ${e.message}", e)
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error: ${e.message}", e)
            }
        }
    }

    private suspend fun handleGoogleSignin(
        result: GetCredentialResponse,
        googleLogin: () -> Unit
    ) {
        val credential = result.credential
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val googleTokenId = googleTokenCredential.idToken

            val authCredential = GoogleAuthProvider.getCredential(googleTokenId, null)
            val user = FirebaseAuth.getInstance().signInWithCredential(authCredential)
                .await().user

            user?.let {
                if (!it.isAnonymous) googleLogin()
            } ?: Log.e(TAG, "Google Sign-In failed: User is null")
        } else {
            Log.e(TAG, "Unexpected credential type")
        }
    }

    private fun getCredentialOptions(context: Context): CredentialOption {
        return GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(false)
            .setServerClientId(context.getString(R.string.google_web_client_id))
            .build()
    }

    private fun getIntent(): Intent {
        return Intent(Settings.ACTION_ADD_ACCOUNT).apply {
            putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
        }
    }

    private fun hasGoogleAccounts(context: Context): Boolean {
        val accounts = AccountManager.get(context).getAccountsByType("com.google")
        return accounts.isNotEmpty()
    }

    private fun isGooglePlayServicesAvailable(context: Context): Boolean {
        val availability = GoogleApiAvailability.getInstance()
        return availability.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
    }

    fun getGoogleSignInLauncher(
        activity: ComponentActivity,
        onResult: (Boolean) -> Unit
    ): ActivityResultLauncher<Intent> {
        return activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "Account picker returned successfully.")
                onResult(true)
            } else {
                Log.w(TAG, "Account picker was canceled.")
                onResult(false)
            }
        }
    }
}
