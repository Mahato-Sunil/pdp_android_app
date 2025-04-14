//package com.officialsunil.pdpapplication.utils
//
//import android.content.Context
//import android.content.Intent
//import android.provider.Settings
//import android.util.Log
//import androidx.activity.result.ActivityResultLauncher
//import androidx.credentials.CredentialManager
//import androidx.credentials.CredentialOption
//import androidx.credentials.CustomCredential
//import androidx.credentials.GetCredentialRequest
//import androidx.credentials.GetCredentialResponse
//import androidx.credentials.exceptions.GetCredentialException
//import androidx.credentials.exceptions.NoCredentialException
//import com.google.android.libraries.identity.googleid.GetGoogleIdOption
//import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.GoogleAuthProvider
//import com.officialsunil.pdpapplication.R
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.tasks.await
//
//object  GoogleAuthUtils {
//        private const val TAG = "GoogleAuthUtils"
//
//        fun initiateGoogleSignin(
//            context: Context,
//            scope: CoroutineScope,
//            launcher: ActivityResultLauncher<Intent>?,
//            googleLogin: () -> Unit
//        ) {
//            val credentialManager = CredentialManager.create(context)
//
//            // Build the GetCredentialRequest with the Google ID Token option
//            val request =
//                GetCredentialRequest.Builder().addCredentialOption(getCredentialOptions(context))
//                    .build()
//
//            // Launch a coroutine to perform the credential request
//            scope.launch {
//                try {
//                    // Attempt to get credentials from the Credential Manager
//                    val result = credentialManager.getCredential(context, request)
//                    handleGoogleSignin(result, googleLogin)
//                } catch (e: NoCredentialException) {
//                    // If no credentials are available, launch the account picker intent
//                    Log.w(TAG, "No credentials found, launching account picker", e)
//
//                    Log.w(TAG, "No credentials found, launching account picker")
//                    launcher?.launch(getIntent())
//                } catch (e: GetCredentialException) {
//                    // Log errors related to credential fetching
//                    Log.e(TAG, "Credential error: ${e.message}", e)
//                } catch (e: Exception) {
//                    // Handle any unexpected exceptions
//                    Log.e(TAG, "Unexpected error: ${e.message}", e)
//                }
//            }
//        }
//
//        // function to  handle the sign in
//        private suspend fun handleGoogleSignin(result: GetCredentialResponse, googleLogin: () -> Unit) {
//            val credential = result.credential
//
//            when (credential) {
//                //google id token credential
//                is CustomCredential -> {
//                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
//                        // Create a GoogleIdTokenCredential instance from the credential data
//                        val googleTokenCredential =
//                            GoogleIdTokenCredential.createFrom(credential.data)
//                        val googleTokenId = googleTokenCredential.idToken
//
//                        // Use the Google ID token to authenticate with Firebase
//                        val authCredential = GoogleAuthProvider.getCredential(googleTokenId, null)
//                        val user = FirebaseAuth.getInstance().signInWithCredential(authCredential)
//                            .await().user
//
//                        user?.let {
//                            if (it.isAnonymous.not()) googleLogin.invoke()
//                        } ?: run {
//                            Log.e(TAG, "Google Sign-In failed: User is null")
//                        }
//                    } else {
//                        Log.e(TAG, "Credential type is not Google ID Token")
//                    }
//                }
//
//                else -> {
//                    Log.e(TAG, "Credential is not of CustomCredential type")
//                }
//            }
//        }
//
//        private fun getCredentialOptions(context: Context): CredentialOption {
//            return GetGoogleIdOption.Builder()
//                .setFilterByAuthorizedAccounts(false) // Include accounts not authorized previously
//                .setAutoSelectEnabled(false) // Disable automatic account selection
//                .setServerClientId(context.getString(R.string.google_web_client_id)) // Server client ID from strings.xml
//                .build()
//        }
//
//        private fun getIntent(): Intent {
//            return Intent(Settings.ACTION_ADD_ACCOUNT).apply {
//                putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
//            }
//        }
//
//
//        fun getGoogleSignInLauncher(
//            activity: ComponentActivity,
//            onResult: (Boolean) -> Unit
//        ) = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                Log.d("GoogleAuth", "Account picker returned successfully.")
//                onResult(true)
//            } else {
//                Log.w("GoogleAuth", "Account picker was canceled.")
//                onResult(false)
//            }
//        }
//
//        fun initiateGoogleSignin(
//            activity: Activity,
//            launcher: ActivityResultLauncher<Intent>
//        ) {
//            val signInIntent = /* create Google SignIn Intent */
//                launcher.launch(signInIntent)
//        }
//    }
//
//}


package com.officialsunil.pdpapplication.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.credentials.*
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
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
            } catch (e: NoCredentialException) {
                Log.w(TAG, "No credentials found, launching account picker")
                launcher?.launch(getIntent())
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
