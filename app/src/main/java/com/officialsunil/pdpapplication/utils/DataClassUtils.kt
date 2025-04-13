package com.officialsunil.pdpapplication.utils

// all the data class to be stored here
import androidx.compose.ui.graphics.vector.ImageVector

// create a data class for the account information section
data class ProfileInformation(
    val icon : ImageVector,
    val title : String,
    val description : String
)

// data class for the rational description
data class SigninigRationale(
    val description: String
)

// data class for the users credentials
data class CurrentUserCredentials(
    val uid : String,
    val name : String,
    val email : String,
    val phone : String ,
    val photoUrl : String,
    val isEmailVerified : Boolean

)