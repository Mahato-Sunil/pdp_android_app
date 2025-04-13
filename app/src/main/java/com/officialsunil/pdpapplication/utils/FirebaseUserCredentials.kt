package com.officialsunil.pdpapplication.utils

import android.util.Log
import com.google.firebase.auth.FirebaseAuth

// this class handles the code for firebase user  credentials
object FirebaseUserCredentials {
    val TAG = "FirebaseUserCredentials"

    val auth = FirebaseAuth.getInstance()

    // function to check  registered users
    fun checkCurrentlySignedInUser(): Boolean {
        val currentUser = auth.currentUser
        return currentUser != null
    }

    // function to retrieve the current users details
    fun getCurrentUserCredentails(): CurrentUserCredentials? {
        lateinit var currentUserCredentials : CurrentUserCredentials
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
}