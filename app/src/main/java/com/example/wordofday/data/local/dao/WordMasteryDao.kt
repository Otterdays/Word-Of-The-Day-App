package com.example.wordofday.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.wordofday.data.local.entity.WordMasteryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordMasteryDao {
    @Query("SELECT * FROM word_mastery WHERE nextReviewDay <= :todayEpochDay ORDER BY nextReviewDay ASC LIMIT :limit")
    suspend fun getDue(todayEpochDay: Long, limit: Int = 50): List<WordMasteryEntity>

    @Query("SELECT COUNT(*) FROM word_mastery WHERE nextReviewDay <= :todayEpochDay")
    fun observeDueCount(todayEpochDay: Long): Flow<Int>

    @Query("SELECT * FROM word_mastery WHERE wordKey = :key LIMIT 1")
    suspend fun getByKey(key: String): WordMasteryEntity?

    @Query("SELECT * FROM word_mastery ORDER BY masteryLevel DESC, correctCount DESC LIMIT :limit")
    suspend fun getTopMastered(limit: Int = 20): List<WordMasteryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: WordMasteryEntity)

    @Query("SELECT COUNT(*) FROM word_mastery WHERE masteryLevel >= 3")
    fun observeMasteredCount(): Flow<Int>
}
