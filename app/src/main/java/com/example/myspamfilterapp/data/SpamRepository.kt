package com.example.myspamfilterapp.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SpamRepository(private val db: SpamDatabase) {
//    val spamCalls: Flow<List<SpamCall>> = flow{
//        emit(db.spamCallDao().getAllCalls())
//    }
    val spamCalls: Flow<List<SpamCall>> = db.spamCallDao().getAllCalls()
}