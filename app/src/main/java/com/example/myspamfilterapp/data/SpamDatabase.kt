package com.example.myspamfilterapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Room database for storing spam call information.
 *
 * This database contains a single table, `spam_calls`, represented by the [SpamCall] entity.
 * It provides access to [SpamCallDao] for performing database operations such as insertions,
 * queries, and updates.
 *
 * The database is implemented as a singleton to ensure a single instance throughout the app.
 */
@Database(entities = [SpamCall::class], version = 2, exportSchema = false)
abstract class SpamDatabase : RoomDatabase() {

    /**
     * Returns the [SpamCallDao] for accessing spam call data.
     */
    abstract fun spamCallDao(): SpamCallDao

    companion object {
        @Volatile
        private var INSTANCE: SpamDatabase? = null

        /**
         * Migration from database version 1 to 2.
         *
         * Adds a new column `callCount` to the `spam_calls` table with a default value of 1.
         */
        val MIGRATION_1_2 = object : Migration(1,2){
            override fun migrate(db: SupportSQLiteDatabase){
                db.execSQL("ALTER TABLE spam_calls ADD COLUMN callCount INTEGER NOT NULL DEFAULT 1")
            }
        }

        /**
         * Retrieves the singleton instance of [SpamDatabase].
         *
         * If the database does not yet exist, it will be created with the appropriate migrations.
         *
         * @param context The application context used to create or retrieve the database.
         * @return The singleton [SpamDatabase] instance.
         */
        fun get(context: Context): SpamDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SpamDatabase::class.java,
                    "spam_calls.db"
                ).addMigrations(MIGRATION_1_2).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
