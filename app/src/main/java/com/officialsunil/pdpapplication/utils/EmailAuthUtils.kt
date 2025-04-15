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
import com.officialsunil.pdpapplication.viewui.MainActivity
import java.io.SyncFailedException

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
                onFailure(task.exception)
            }
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun signOut(context: Context) {
        auth.signOut()

        Toast.makeText(context, "Signed Out Successfully", Toast.LENGTH_LONG).show()

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("SKIP_SPLASH", true)
        }

        context.startActivity(intent)

        if (context is Activity) {
            context.finish() // Finish the current activity if context is an activity
        }
    }


    fun navigateToRegistrationActivity(context: Context, activity: Activity) {
        val registrationIntent = Intent(context, AccountRegistrationActivity::class.java)
        context.startActivity(registrationIntent)
        activity.finish()
    }

    //  data validation
    fun checkCredentials(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        onSuccess: () -> Unit,
        onFailure: (List<String>) -> Unit
    ) {
        // define the
        val nameRegex = "^[a-zA-Z\\s'-]{2,}$".toRegex()
        val emailRegex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}".toRegex()
        val passwordRegex =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$".toRegex()

        if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
            val isValidName = name.trim().matches(nameRegex)
            val isValidEmail = email.trim().matches(emailRegex)
            val isValidPassword = password.trim().matches(passwordRegex)
            val isPasswordMatched = password == confirmPassword

            val errors = mutableListOf<String>()

            if (!isValidName) errors.add("Invalid name format.")
            if (!isValidEmail) errors.add("Invalid email format.")
            if (!isValidPassword) errors.add("Password must be 8+ characters with uppercase, lowercase, number, and special char.")
            if (!isPasswordMatched) errors.add("Passwords do not match.")

            if (isValidName && isValidEmail && isValidPassword && isPasswordMatched) onSuccess()
            else onFailure(errors)
        } else
            onFailure(listOf("Please fill all the fields."))
    }
}
