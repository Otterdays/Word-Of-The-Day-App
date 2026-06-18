package com.example.wordofday.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word_notes")
data class WordNoteEntity(
    @PrimaryKey val wordKey: String,
    val userSentence: String = "",
    val markedHard: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis(),
)
