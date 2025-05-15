package com.officialsunil.pdpapplication.utils

import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlin.getValue

// define the database schema and structure here

@AutoMigration()
@Database(
    entities = [Predictions::class],
    version = 2,
    autoMigrations = [
        AutoMigration(
            from = 1,
            to = 2
        )],
    exportSchema = true
)

// defien the database interface
abstract class SQLiteDatabaseSchema : RoomDatabase() {
    abstract val sqliteDatabaseInterface: SQLiteDatabaseInterface
}

