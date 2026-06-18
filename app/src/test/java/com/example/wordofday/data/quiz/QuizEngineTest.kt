package com.example.wordofday.data.quiz

import com.example.wordofday.data.model.GradeLevel
import com.example.wordofday.data.model.QuizMode
import com.example.wordofday.data.model.WordEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizEngineTest {

    private val pool = listOf(
        WordEntry("alpha", "noun", definition = "First letter word", gradeLevel = GradeLevel.GRADE_5),
        WordEntry("beta", "noun", definition = "Second letter word", gradeLevel = GradeLevel.GRADE_5),
        WordEntry("gamma", "noun", definition = "Third letter word", gradeLevel = GradeLevel.GRADE_5),
        WordEntry("delta", "noun", definition = "Fourth letter word", gradeLevel = GradeLevel.GRADE_5),
        WordEntry("epsilon", "noun", definition = "Fifth letter word", gradeLevel = GradeLevel.GRADE_5),
    )

    @Test
    fun `buildSession returns up to five questions`() {
        val session = QuizEngine.buildSession(pool, random = kotlin.random.Random(1))
        assertTrue(session.isNotEmpty())
        assertTrue(session.size <= QuizEngine.QUESTIONS_PER_SESSION)
    }

    @Test
    fun `buildQuestion marks correct definition`() {
        val prompt = pool.first()
        val question = QuizEngine.buildQuestion(prompt, pool, random = kotlin.random.Random(0))
        assertEquals(prompt.definition, question.options[question.correctIndex])
        assertEquals(4, question.options.size)
    }

    @Test
    fun `reverse question uses word options`() {
        val prompt = pool.first()
        val question = QuizEngine.buildQuestion(prompt, pool, QuizMode.REVERSE, random = kotlin.random.Random(0))
        assertEquals(prompt.word, question.options[question.correctIndex])
        assertEquals(QuizMode.REVERSE, question.mode)
    }
}
