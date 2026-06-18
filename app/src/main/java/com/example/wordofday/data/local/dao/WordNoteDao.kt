package com.example.wordofday.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.wordofday.data.local.entity.WordNoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordNoteDao {
    @Query("SELECT * FROM word_notes WHERE wordKey = :key LIMIT 1")
    suspend fun getByKey(key: String): WordNoteEntity?

    @Query("SELECT * FROM word_notes WHERE wordKey = :key LIMIT 1")
    fun observeByKey(key: String): Flow<WordNoteEntity?>

    @Query("SELECT * FROM word_notes WHERE markedHard = 1")
    suspend fun getHardWords(): List<WordNoteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: WordNoteEntity)
}
