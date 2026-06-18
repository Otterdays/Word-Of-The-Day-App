package com.example.wordofday.ui.home

import com.example.wordofday.data.model.GradeLevel
import com.example.wordofday.data.model.WordEntry
import org.junit.Assert.assertTrue
import org.junit.Test

class ShareFormatterTest {

    private val sample = WordEntry(
        word = "Habitat",
        partOfSpeech = "noun",
        pronunciation = "/hab/",
        definition = "Where an animal lives.",
        example = "The forest is a habitat.",
        etymology = "From Latin habitare.",
        gradeLevel = GradeLevel.GRADE_3,
    )

    @Test
    fun `kid format is playful`() {
        val text = ShareFormatter.format(sample, GradeLevel.GRADE_1)
        assertTrue(text.contains("Today I learned"))
        assertTrue(text.contains("📚"))
    }

    @Test
    fun `middle format includes example`() {
        val text = ShareFormatter.format(sample, GradeLevel.GRADE_6)
        assertTrue(text.contains("Habitat (noun)"))
        assertTrue(text.contains("Example:"))
    }

    @Test
    fun `adult format includes etymology`() {
        val text = ShareFormatter.format(sample, GradeLevel.ADULT)
        assertTrue(text.contains("Etymology:"))
    }
}
