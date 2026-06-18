package com.example.wordofday.data.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WordSelectionIndexTest {

    @Test
    fun `selection index stays inside pool bounds`() {
        repeat(120) { offset ->
            val index = selectionIndex(
                dayOfYear = 170,
                rotationOffset = offset,
                poolSize = 37,
                salt = 4,
            )

            assertTrue(index in 0 until 37)
        }
    }

    @Test
    fun `rotation offset spreads refreshes across the pool`() {
        val picks = (0 until 12)
            .map { offset ->
                selectionIndex(
                    dayOfYear = 170,
                    rotationOffset = offset,
                    poolSize = 17,
                )
            }
            .toSet()

        assertTrue(picks.size > 8)
    }

    @Test
    fun `category salt changes same-day picks across selected topics`() {
        val sciencePick = selectionIndex(
            dayOfYear = 170,
            rotationOffset = 2,
            poolSize = 53,
            salt = 3,
        )
        val sportsPick = selectionIndex(
            dayOfYear = 170,
            rotationOffset = 2,
            poolSize = 53,
            salt = 9,
        )

        assertNotEquals(sciencePick, sportsPick)
    }

    @Test
    fun `empty pool returns zero defensively`() {
        assertEquals(0, selectionIndex(dayOfYear = 170, rotationOffset = 2, poolSize = 0))
    }
}
