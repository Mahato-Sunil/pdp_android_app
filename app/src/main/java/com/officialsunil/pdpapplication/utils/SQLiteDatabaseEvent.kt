package com.officialsunil.pdpapplication.utils

import android.graphics.Bitmap
import com.google.firebase.Timestamp

/*
This interface is responsibel to define the interaction of users and
other various database events
 */
sealed interface SQLiteDatabaseEvent {
    object savePrediction : SQLiteDatabaseEvent
    data class setUserId(val userId: String) : SQLiteDatabaseEvent
    data class setPredictedName(val predictedName: String) : SQLiteDatabaseEvent
    data class setPredictedImage(val predictedImage: Bitmap) : SQLiteDatabaseEvent
    data class setAccuracy(val accuracy: String) : SQLiteDatabaseEvent
    data class setTimestamp(val timestamp: Timestamp) : SQLiteDatabaseEvent
    data class deletePrediction(val predictions: Predictions) : SQLiteDatabaseEvent
    data class sortPrediction(val sortType: SortType) : SQLiteDatabaseEvent
}