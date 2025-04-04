package com.officialsunil.pdpapplication.utils

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialOption
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
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

class GoogleAuthUtils {
    companion object {
        private const val TAG = "GoogleAuthUtils"
        fun initiateGoogleSignin(
            context: Context,
            scope: CoroutineScope,
            launcher: ActivityResultLauncher<Intent>?,
            googleLogin: () -> Unit
        ) {
            val credentialManager = CredentialManager.create(context)

            // Build the GetCredentialRequest with the Google ID Token option
            val request =
                GetCredentialRequest.Builder().addCredentialOption(getCredentialOptions(context))
                    .build()

            // Launch a coroutine to perform the credential request
            scope.launch {
                try {
                    // Attempt to get credentials from the Credential Manager
                    val result = credentialManager.getCredential(context, request)
                    handleGoogleSignin(result, googleLogin)
                } catch (e: NoCredentialException) {
                    // If no credentials are available, launch the account picker intent
                    Log.w(TAG, "No credentials found, launching account picker")
                    launcher?.launch(getIntent())
                } catch (e: GetCredentialException) {
                    // Log errors related to credential fetching
                    Log.e(TAG, "Credential error: ${e.message}", e)
                } catch (e: Exception) {
                    // Handle any unexpected exceptions
                    Log.e(TAG, "Unexpected error: ${e.message}", e)
                }
            }
        }

        // function to  handle the sign in
        private suspend fun handleGoogleSignin(result: GetCredentialResponse, googleLogin: () -> Unit) {
            val credential = result.credential

            when (credential) {
                //google id token credential
                is CustomCredential -> {
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        // Create a GoogleIdTokenCredential instance from the credential data
                        val googleTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        val googleTokenId = googleTokenCredential.idToken

                        // Use the Google ID token to authenticate with Firebase
                        val authCredential = GoogleAuthProvider.getCredential(googleTokenId, null)
                        val user = FirebaseAuth.getInstance().signInWithCredential(authCredential)
                            .await().user

                        user?.let {
                            if (it.isAnonymous.not()) googleLogin.invoke()
                            // If the user is successfully authenticated, invoke the callback
//                                    Log.d(TAG, "Google Sign-In successful: ${it.email}")

//                                    googleLogin.invoke()
                        } ?: run {
                            Log.e(TAG, "Google Sign-In failed: User is null")
                        }
                    } else {
                        Log.e(TAG, "Credential type is not Google ID Token")
                    }
                }

                else -> {
                    Log.e(TAG, "Credential is not of CustomCredential type")
                }
            }
        }


        /**
         * Creates a Google ID credential option for the Credential Manager.
         *
         * @param context Context of the activity or application.
         * @return CredentialOption configured for Google ID tokens.
         */
        private fun getCredentialOptions(context: Context): CredentialOption {
            return GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false) // Include accounts not authorized previously
                .setAutoSelectEnabled(false) // Disable automatic account selection
                .setServerClientId(context.getString(R.string.google_web_client_id)) // Server client ID from strings.xml
                .build()
        }

        /**
         * Returns an intent to launch the account picker when no credentials are available.
         *
         * @return Intent for adding a Google account.
         */
        private fun getIntent(): Intent {
            return Intent(Settings.ACTION_ADD_ACCOUNT).apply {
                putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
            }
        }
    }
}