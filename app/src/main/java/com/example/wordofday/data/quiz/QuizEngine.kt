package com.example.wordofday.data.quiz

import com.example.wordofday.data.model.QuizQuestion
import com.example.wordofday.data.model.WordEntry
import kotlin.random.Random

// [TRACE: DOCS/ROADMAP.md] — §10c multiple-choice quiz generation
object QuizEngine {

    const val QUESTIONS_PER_SESSION = 5

    fun buildSession(pool: List<WordEntry>, random: Random = Random.Default): List<QuizQuestion> {
        if (pool.size < 4) return emptyList()
        val prompts = pool.shuffled(random).take(QUESTIONS_PER_SESSION.coerceAtMost(pool.size))
        return prompts.map { word -> buildQuestion(word, pool, random) }
    }

    fun buildQuestion(
        prompt: WordEntry,
        pool: List<WordEntry>,
        random: Random = Random.Default,
    ): QuizQuestion {
        val distractors = pool
            .filter { it.word != prompt.word && it.definition != prompt.definition }
            .shuffled(random)
            .take(3)
            .map { it.definition }

        val options = (distractors + prompt.definition).shuffled(random)
        return QuizQuestion(
            promptWord = prompt,
            options = options,
            correctIndex = options.indexOf(prompt.definition),
        )
    }
}
