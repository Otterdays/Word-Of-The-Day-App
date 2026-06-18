package com.example.wordofday.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GradeLevelOffsetTest {

    @Test
    fun `withOffset stays within enum bounds`() {
        assertEquals(GradeLevel.PRE_K, GradeLevel.PRE_K.withOffset(-5))
        assertEquals(GradeLevel.ADULT, GradeLevel.ADULT.withOffset(5))
    }

    @Test
    fun `withOffset moves one step`() {
        assertEquals(GradeLevel.GRADE_5, GradeLevel.GRADE_4.withOffset(1))
        assertEquals(GradeLevel.GRADE_3, GradeLevel.GRADE_4.withOffset(-1))
    }

    @Test
    fun `shift helpers respect boundaries`() {
        assertFalse(GradeLevel.PRE_K.canShiftEasier())
        assertTrue(GradeLevel.PRE_K.canShiftHarder())
        assertTrue(GradeLevel.ADULT.canShiftEasier())
        assertFalse(GradeLevel.ADULT.canShiftHarder())
    }
}
