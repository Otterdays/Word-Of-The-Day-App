package com.example.wordofday.data.model

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
)

data class AchievementProgress(
    val achievement: Achievement,
    val unlocked: Boolean,
    val unlockedAt: Long? = null,
)

data class WeeklyProgress(
    val wordsViewed: Int,
    val quizAttempts: Int,
    val quizCorrect: Int,
    val accuracyPercent: Int,
    val streakDays: Int,
    val masteredWords: Int,
    val dueReviews: Int,
)

data class LearningDashboard(
    val dueReviewCount: Int,
    val masteredCount: Int,
    val weekly: WeeklyProgress,
    val recentAchievements: List<AchievementProgress>,
)
