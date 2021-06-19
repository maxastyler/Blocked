package com.example.blocked.repository

import com.example.blocked.database.Score
import com.example.blocked.database.ScoreDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ScoreRepository @Inject constructor(private val scoreDatabase: ScoreDatabase) {
    fun getScores(number: Int? = null): Flow<List<Score>> = when (number) {
        null -> scoreDatabase.scoreDao().getAll()
        else -> scoreDatabase.scoreDao().getN(number)
    }

    suspend fun addScore(score: Score) = scoreDatabase.scoreDao().insert(score)
}