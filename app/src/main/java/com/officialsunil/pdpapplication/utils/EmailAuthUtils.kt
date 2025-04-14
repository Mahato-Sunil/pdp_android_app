package com.officialsunil.pdpapplication.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.officialsunil.pdpapplication.viewui.AccountRegistrationActivity

object EmailAuthUtils {
    private var auth: FirebaseAuth = Firebase.auth
    private const val TAG = "EmailAuthUtils"


    fun registerWithEmail(
        context: Context,
        name: String,
        email: String,
        password: String,
        onSuccess: (FirebaseUser?) -> Unit,
        onFailure: (Exception?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(name).build()
                user?.updateProfile(profileUpdates)?.addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful) {
                        onSuccess(user)
                        Log.d(TAG, "User profile created and updated.")
                    } else Log.w(
                        TAG, "Failed to update user profile.", updateTask.exception
                    )
                }
            } else {
                Log.w(TAG, "Registration failed", task.exception)
                Toast.makeText(
                    context, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT
                ).show()
                onFailure(task.exception)
            }
        }
    }

    fun loginWithEmail(
        context: Context,
        email: String,
        password: String,
        onSuccess: (FirebaseUser?) -> Unit,
        onFailure: (Exception?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                onSuccess(user)
            } else {
                Log.w(TAG, "Login failed", task.exception)
                Toast.makeText(
                    context, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT
                ).show()
                onFailure(task.exception)
            }
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun signOut() {
        auth.signOut()
    }

    fun navigateToRegistrationActivity(context: Context, activity: Activity) {
        val registrationIntent = Intent(context, AccountRegistrationActivity::class.java)
        context.startActivity(registrationIntent)
        activity.finish()
    }
}
