package com.officialsunil.pdpapplication.utils

/*
This interface is responsibel to define the interaction of users and
other various database events
 */
sealed interface SQLiteDatabaseEvent {
//    data class MigrateDatabase(val versionOld: Int, val versionNew: Int): SQLiteDatabaseEvent
    object SavePrediction : SQLiteDatabaseEvent
    data class SetUserId(val userId: String) : SQLiteDatabaseEvent
    data class SetDiseaseId(val diseaseId: String) : SQLiteDatabaseEvent
    data class SetPredictedName(val predictedName: String) : SQLiteDatabaseEvent
    data class SetPredictedImage(val predictedImage: ByteArray) : SQLiteDatabaseEvent
    data class SetAccuracy(val accuracy: String) : SQLiteDatabaseEvent
    data class SetTimestamp(val timestamp: String) : SQLiteDatabaseEvent
    data class DeletePrediction(val predictions: Predictions) : SQLiteDatabaseEvent
    data class SortPrediction(val sortType: SortType) : SQLiteDatabaseEvent
}