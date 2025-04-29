package com.officialsunil.pdpapplication.utils

import android.content.Context
import androidx.room.Room

object SQLiteDatabaseObjectProvider {
    @Volatile
    private var INSTANCE: SQLiteDatabaseSchema? = null

    fun getDatabase(context: Context): SQLiteDatabaseSchema {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                SQLiteDatabaseSchema::class.java,
                "predictions.db"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}
