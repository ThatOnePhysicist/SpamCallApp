package com.example.myspamfilterapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


/**
 * Data Access Object (DAO) for interacting with the `spam_calls` table in the Room database.
 *
 * Provides methods for inserting new spam call entries, querying existing calls,
 * and updating metadata such as call counts.
 */
@Dao
interface SpamCallDao {

    /**
     * Inserts a new [SpamCall] entry into the database.
     * If an entry with the same primary key already exists, it will be replaced.
     *
     * @param spamCall
     * The [SpamCall] object to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(spamCall: SpamCall)


    /**
     * Returns a flow of all spam calls in the database, ordered by timestamp descending.
     *
     * @return
     * [Flow] of [List] of [SpamCall] objects.
     */
    @Query("SELECT * FROM spam_calls ORDER BY timestamp DESC")
    fun getAllCalls(): Flow<List<SpamCall>>

    /**
     * Retrieves the most recent call entry for the given phone number.
     *
     * @param number
     * The phone number to search for.
     *
     * @return
     * The last [SpamCall] for the number, or null if none exists.
     */
    @Query("SELECT * FROM spam_calls WHERE phoneNumber = :number ORDER BY id DESC LIMIT 1")
    suspend fun getLastCall(number: String): SpamCall?

    /**
     * Updates the `callCount` field for a specific spam call entry.
     *
     * @param id
     * The database ID of the spam call entry to update.
     *
     * @param newCount
     * The new call count to set.
     */
    @Query("UPDATE spam_calls SET callCount=:newCount WHERE id = :id")
    suspend fun updateCallCount(id: Int, newCount: Int)
}