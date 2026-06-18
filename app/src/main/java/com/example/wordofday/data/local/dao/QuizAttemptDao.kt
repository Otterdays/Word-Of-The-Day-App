package com.example.wordofday.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.wordofday.data.local.entity.QuizAttemptEntity

@Dao
interface QuizAttemptDao {
    @Insert
    suspend fun insert(entity: QuizAttemptEntity)

    @Query("SELECT COUNT(*) FROM quiz_attempts WHERE timestamp >= :sinceMs")
    suspend fun countSince(sinceMs: Long): Int

    @Query("SELECT COUNT(*) FROM quiz_attempts WHERE correct = 1 AND timestamp >= :sinceMs")
    suspend fun countCorrectSince(sinceMs: Long): Int

    @Query("SELECT COUNT(*) FROM quiz_attempts")
    suspend fun totalAttempts(): Int
}
