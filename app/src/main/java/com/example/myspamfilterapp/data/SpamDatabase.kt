package com.example.myspamfilterapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [SpamCall::class], version = 2, exportSchema = false)
abstract class SpamDatabase : RoomDatabase() {

    abstract fun spamCallDao(): SpamCallDao

    companion object {
        @Volatile
        private var INSTANCE: SpamDatabase? = null
        val MIGRATION_1_2 = object : Migration(1,2){
            override fun migrate(db: SupportSQLiteDatabase){
                db.execSQL("ALTER TABLE spam_calls ADD COLUMN callCount INTEGER NOT NULL DEFAULT 1")
            }
        }
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
