package com.example.blocked.repository

import com.example.blocked.database.Score
import com.example.blocked.database.ScoreDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ScoreRepository @Inject constructor(private val scoreDatabase: ScoreDatabase) {
    fun getScores(): Flow<Score> = scoreDatabase.scoreDao().getAll()
    fun addScore(score: Score) = scoreDatabase.scoreDao().insert(score)
}