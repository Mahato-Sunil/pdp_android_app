package com.officialsunil.pdpapplication.utils

/*
Data Access Object (DAO) for the SQLite database
helps users to interact with the database
defines method for inserting,, deleting and  getting the  results
 */

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SQLiteDatabaseInterface {

    //define the methods that would be used for working witht the database
    @Upsert
    suspend fun insertPredicitions(predictions: Predictions)

    @Delete
    suspend fun deletePrediction(predictions: Predictions)

    @Query("SELECT* FROM Predictions WHERE userId = :userId ORDER BY name ASC" )
    suspend fun getPredictionListOrderedByName(userId : String): Flow<List<Predictions>>

    @Query("SELECT* FROM Predictions WHERE userId = :userId ORDER BY timestamp DESC" )
    suspend fun getPredictionListOrderedByTimestamp(userId : String): Flow<List<Predictions>>
}