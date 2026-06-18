package com.example.wordofday.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.wordofday.data.local.dao.QuizAttemptDao
import com.example.wordofday.data.local.dao.WordMasteryDao
import com.example.wordofday.data.local.dao.WordNoteDao
import com.example.wordofday.data.local.entity.QuizAttemptEntity
import com.example.wordofday.data.local.entity.WordMasteryEntity
import com.example.wordofday.data.local.entity.WordNoteEntity

@Database(
    entities = [
        WordMasteryEntity::class,
        WordNoteEntity::class,
        QuizAttemptEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class WordOfDayDatabase : RoomDatabase() {
    abstract fun wordMasteryDao(): WordMasteryDao
    abstract fun wordNoteDao(): WordNoteDao
    abstract fun quizAttemptDao(): QuizAttemptDao

    companion object {
        @Volatile
        private var instance: WordOfDayDatabase? = null

        fun get(context: Context): WordOfDayDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    WordOfDayDatabase::class.java,
                    "wordofday_learning.db",
                ).build().also { instance = it }
            }
    }
}
