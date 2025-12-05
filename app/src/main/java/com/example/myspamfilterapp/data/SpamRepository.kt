package com.example.myspamfilterapp.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository providing access to spam call data.
 *
 * This class abstracts access to the [SpamDatabase] and exposes a reactive [Flow] of
 * all spam calls stored in the database.
 *
 * @property db The [SpamDatabase] instance used for database operations.
 */
class SpamRepository(private val db: SpamDatabase) {

    /**
     * A [Flow] emitting the list of all [SpamCall] records in the database.
     *
     * The list is ordered by timestamp in descending order as defined by
     * [SpamCallDao.getAllCalls].
     */
    val spamCalls: Flow<List<SpamCall>> = db.spamCallDao().getAllCalls()
}