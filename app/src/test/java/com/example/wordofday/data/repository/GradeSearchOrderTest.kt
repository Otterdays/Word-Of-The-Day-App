package com.example.wordofday.data.repository

import com.example.wordofday.data.model.GradeLevel
import org.junit.Assert.assertEquals
import org.junit.Test

class GradeSearchOrderTest {

    @Test
    fun `starts at primary then expands outward symmetrically`() {
        val order = gradeSearchOrder(GradeLevel.GRADE_8)
        assertEquals(GradeLevel.GRADE_8, order[0])
        assertEquals(GradeLevel.GRADE_7, order[1])
        assertEquals(GradeLevel.GRADE_9, order[2])
        assertEquals(GradeLevel.GRADE_6, order[3])
        assertEquals(GradeLevel.GRADE_10, order[4])
    }

    @Test
    fun `boundary primary pre_k grows toward higher grades only`() {
        val order = gradeSearchOrder(GradeLevel.PRE_K)
        assertEquals(GradeLevel.PRE_K, order[0])
        assertEquals(GradeLevel.KINDERGARTEN, order[1])
        assertEquals(GradeLevel.GRADE_1, order[2])
    }

    @Test
    fun `boundary primary adult grows toward lower grades only`() {
        val order = gradeSearchOrder(GradeLevel.ADULT)
        assertEquals(GradeLevel.ADULT, order[0])
        assertEquals(GradeLevel.GRADE_12, order[1])
        assertEquals(GradeLevel.GRADE_11, order[2])
    }

    @Test
    fun `includes every grade exactly once`() {
        val order = gradeSearchOrder(GradeLevel.GRADE_5)
        assertEquals(GradeLevel.entries.size, order.size)
        assertEquals(GradeLevel.entries.toSet(), order.toSet())
    }
}
