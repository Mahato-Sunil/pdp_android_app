package com.officialsunil.pdpapplication.utils.firebase

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.officialsunil.pdpapplication.utils.CurrentUserCredentials
import com.officialsunil.pdpapplication.viewui.MainActivity

// this class handles the code for firebase user  credentials
object FirebaseUserCredentials {
    private const val TAG = "FirebaseUserCredentials"

    val auth = FirebaseAuth.getInstance()

    // function to check  registered users
    fun checkCurrentlySignedInUser(): Boolean {
        val currentUser = auth.currentUser
        return currentUser != null
    }

    // function to retrieve the current users details
    fun getCurrentUserCredentails(): CurrentUserCredentials? {
        lateinit var currentUserCredentials: CurrentUserCredentials
        // check the current user
        if (!checkCurrentlySignedInUser()) {
            Log.d(TAG, "No user signed in")
            return null
        }
        val currentUser = auth.currentUser
        currentUser?.let {
            val uid = it.uid
            val name = it.displayName
            val email = it.email
            val phone = it.phoneNumber
            val photoUrl = it.photoUrl
            val isEmailVerified = it.isEmailVerified

            //store it in the data class
            currentUserCredentials = CurrentUserCredentials(
                uid = uid,
                name = name.toString(),
                email = email.toString(),
                phone = phone.toString(),
                photoUrl = photoUrl.toString(),
                isEmailVerified = isEmailVerified
            )
        }
        // return the data class
        return currentUserCredentials
    }

    // function to delete the current logged in user
    fun deleteCurrentUser(
        context: Context,
        email: String,
        password: String,
        onReauthRequired: () -> Unit
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return

        currentUser.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("DeleteAccount", "User account deleted.")
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    context.startActivity(intent)
                    if (context is Activity) context.finish()
                } else {
                    when (val e = task.exception) {
                        is FirebaseAuthRecentLoginRequiredException -> {
                            Log.e("DeleteAccount", "Re-authentication required")
                            onReauthRequired()
                        }

                        else -> {
                            Log.e("DeleteAccount", "Error: ${e?.message}")
                        }
                    }
                }
            }
    }


    fun reauthenticateAndDelete(context: Context, email: String, password: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val credential = EmailAuthProvider.getCredential(email, password)

        currentUser.reauthenticate(credential)
            .addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    Log.d("DeleteAccount", "Re-authenticated successfully")
                    deleteCurrentUser(context, email, password) { }
                } else {
                    Toast.makeText(context, "Failed to Re-Authenticate", Toast.LENGTH_LONG).show()
                    Log.e(
                        "DeleteAccount",
                        "Re-authentication failed: ${reauthTask.exception?.message}"
                    )
                }
            }
    }

}