package com.maxtyler.blocked.repository

import com.maxtyler.blocked.database.BlockedDatabase
import com.maxtyler.blocked.database.Score
import com.maxtyler.blocked.database.ScoreDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ScoreRepository @Inject constructor(private val scoreDatabase: BlockedDatabase) {
    fun getScores(number: Int? = null): Flow<List<Score>> = when (number) {
        null -> scoreDatabase.scoreDao().getAll()
        else -> scoreDatabase.scoreDao().getN(number)
    }

    suspend fun addScore(score: Score) = scoreDatabase.scoreDao().insert(score)
}