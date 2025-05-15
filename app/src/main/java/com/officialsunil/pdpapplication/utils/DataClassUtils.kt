package com.officialsunil.pdpapplication.utils

// all the data class to be stored here
import android.graphics.Bitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Blob
import java.util.UUID


// create a data class for the account information section
data class ProfileInformation(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val isContentEditable: Boolean
)

// data class for the rational description
data class SigninigRationale(
    val description: String
)

// data class for the users credentials
data class CurrentUserCredentials(
    val uid: String,
    val name: String,
    val email: String,
    val phone: String,
    val photoUrl: String,
    val isEmailVerified: Boolean
)

// for the user profile settings
data class UserProfileSettings(
    val title: String, val function: String
)

//for registration activity
data class RegistrationCredentials(
    val key: String, val heading: String, val placeholder: String, val inputType: KeyboardType
)

data class SocialMediaIcon(
    val icon: String, val description: String, val profileUrl: String
)

// data class for firestore databse
data class PredictionData(
    val userId: String,
    val imageListArray: List<Int>,
    val predictedName: String,
    val accuracy: String,
    val timestamp: Timestamp
)

data class RetrievePredictionData(
    val retrievePredictionData: List<PredictionData>
)

data class DiagnosesList(
    val image: Bitmap,
    val name: String,
    val timestamp: Timestamp
)

// data class PRediction for Room local database
// complete alternative of the firebase
// mimicing the firebase data structure
@Entity(tableName = "Predictions")
data class Predictions(
    val userId: String,
    val diseaseId: String?,
    val image: ByteArray,
    val name: String,
    val accuracy: String,
    val timestamp: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)

// data class to store the state of the  database predictions
data class PredictionState(
    val predictions: List<Predictions> = emptyList(),
    val userId: String = "",
    val image: ByteArray = ByteArray(0),
    val name: String = "",
    val accuracy: String = "",
    val timestamp: String = Timestamp.now().toString(),
    val diseaseId: String = "",
    val isStoringPredictions: Boolean = false,
    val sortType: SortType = SortType.TIMESTAMP        // creating the default sort type as Timestampt
)
//
//// data class to store the version  of the database for migration
//data class DatabaseVersion (
//    val versionOld: Int,
//    val versionNew: Int
//)