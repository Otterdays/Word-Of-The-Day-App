package com.example.wordofday.data.content

import com.example.wordofday.data.model.GradeLevel
import com.example.wordofday.data.model.WordEntry
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WordContentQualityTest {

    @Test
    fun `detects placeholder corpus rows`() {
        val thin = WordEntry(
            word = "stream",
            partOfSpeech = "verb",
            definition = "To stream as an action or process, frequent in advanced coursework.",
            example = "Engineers debated how stream affects system design.",
            synonyms = listOf("stream", "related term"),
            usageNote = "Common in tech topics at this reading level.",
            gradeLevel = GradeLevel.GRADE_11,
        )
        assertTrue(WordContentQuality.isThin(thin))
    }

    @Test
    fun `cleaned synonyms drop placeholders`() {
        val entry = WordEntry(
            word = "stream",
            partOfSpeech = "verb",
            definition = "To broadcast media live.",
            synonyms = listOf("stream", "related term", "broadcast"),
        )
        assertFalse(WordContentQuality.cleanedSynonyms(entry).contains("related term"))
        assertTrue(WordContentQuality.cleanedSynonyms(entry).contains("broadcast"))
    }
}
