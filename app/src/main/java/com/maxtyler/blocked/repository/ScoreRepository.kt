package com.maxtyler.blocked.repository

import com.maxtyler.blocked.database.BlockedDatabase
import com.maxtyler.blocked.database.Score
import com.maxtyler.blocked.database.ScoreDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ScoreRepository @Inject constructor(private val scoreDatabase: BlockedDatabase) {

    private val scoreDao: ScoreDao = scoreDatabase.scoreDao()

    fun getScores(number: Int? = null): Flow<List<Score>> = when (number) {
        null -> scoreDao.getAll()
        else -> scoreDao.getN(number)
    }

    suspend fun addScore(score: Score) = scoreDao.insert(score)
}