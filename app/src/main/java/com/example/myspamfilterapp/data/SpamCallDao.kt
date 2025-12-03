//package com.example.myspamfilterapp.data
//
//import androidx.room.*
//
//@Dao
//interface SpamCallDao {
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insert(call: SpamCall)
//
//    @Query("SELECT * FROM spam_calls ORDER BY timestamp DESC")
//    suspend fun getAllCalls(): List<SpamCall>
//
//    @Query("SELECT * FROM spam_calls WHERE isFinalSpam = 1 ORDER BY timestamp DESC")
//    suspend fun getSpamOnly(): List<SpamCall>
//
//    @Delete
//    suspend fun delete(call: SpamCall)
//
//    @Query("DELETE FROM spam_calls")
//    suspend fun clear()
//}


package com.example.myspamfilterapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SpamCallDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(spamCall: SpamCall)

    @Query("SELECT * FROM spam_calls ORDER BY timestamp DESC")
    fun getAllCalls(): Flow<List<SpamCall>>

    @Query("SELECT * FROM spam_calls WHERE phoneNumber = :number ORDER BY id DESC LIMIT 1")
    suspend fun getLastCall(number: String): SpamCall?

    @Query("UPDATE spam_calls SET callCount=:newCount WHERE id = :id")
    suspend fun updateCallCount(id: Int, newCount: Int)
}