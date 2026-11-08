package com.example.wordofday.data.preferences

import com.example.wordofday.data.model.GradeLevel
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HistoryRecordCodecTest {

    @Test
    fun `round trips a record whose wordKey contains a pipe`() {
        val date = LocalDate.of(2026, 11, 3)
        val raw = EngagementRepository.encodeHistoryRecord(date, "ADULT|serendipity", "Serendipity", GradeLevel.ADULT)
        val decoded = EngagementRepository.decodeHistoryRecord(raw)!!
        assertEquals(date, decoded.date)
        assertEquals("ADULT|serendipity", decoded.wordKey)
        assertEquals("Serendipity", decoded.word)
        assertEquals(GradeLevel.ADULT, decoded.gradeLevel)
    }

    @Test
    fun `rejects corrupt records`() {
        assertNull(EngagementRepository.decodeHistoryRecord(""))
        assertNull(EngagementRepository.decodeHistoryRecord("not-a-date|A|b|word|ADULT"))
        assertNull(EngagementRepository.decodeHistoryRecord("2026-11-03|A|b|word|NOPE"))
        assertNull(EngagementRepository.decodeHistoryRecord("2026-11-03|ADULT"))
    }
}
