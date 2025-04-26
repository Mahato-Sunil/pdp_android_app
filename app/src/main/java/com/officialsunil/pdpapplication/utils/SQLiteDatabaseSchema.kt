package com.officialsunil.pdpapplication.utils

import androidx.room.Database
import androidx.room.RoomDatabase

// define the database schema and structure here

@Database(
    entities = [Predictions::class],
    version = 1,
)

abstract class SQLiteDatabaseSchema: RoomDatabase() {
    abstract val sqliteDatabaseInterface: SQLiteDatabaseInterface
}