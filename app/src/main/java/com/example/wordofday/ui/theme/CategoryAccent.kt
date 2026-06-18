package com.example.wordofday.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.wordofday.data.model.Category

// [TRACE: DOCS/ROADMAP.md] — §8b category color accents
object CategoryAccent {
    fun container(category: Category): Color = when (category) {
        Category.MYTHOLOGY -> Color(0xFFFFF3E0)
        Category.LITERATURE -> Color(0xFFF3E5F5)
        Category.PHILOSOPHY -> Color(0xFFE8EAF6)
        Category.SACRED -> Color(0xFFFFFDE7)
        Category.GENERAL -> Color(0xFFE8EAF6)
        Category.TECH -> Color(0xFFE3F2FD)
        Category.SPORTS -> Color(0xFFE8F5E9)
        Category.CARS -> Color(0xFFF3E5F5)
        Category.FOOD -> Color(0xFFFFF3E0)
        Category.SCIENCE -> Color(0xFFE0F7FA)
        Category.SPACE -> Color(0xFFEDE7F6)
        Category.ANIMALS -> Color(0xFFF1F8E9)
        Category.MUSIC -> Color(0xFFFCE4EC)
        Category.HISTORY -> Color(0xFFEFEBE9)
        Category.MATH -> Color(0xFFECEFF1)
        Category.HEALTH -> Color(0xFFFFEBEE)
        Category.WEATHER -> Color(0xFFE1F5FE)
        Category.EMOTIONS -> Color(0xFFFFF8E1)
    }

    fun onContainer(category: Category): Color = when (category) {
        Category.MYTHOLOGY -> Color(0xFFE65100)
        Category.LITERATURE -> Color(0xFF6A1B9A)
        Category.PHILOSOPHY -> Color(0xFF283593)
        Category.SACRED -> Color(0xFFF9A825)
        Category.GENERAL -> Color(0xFF283593)
        Category.TECH -> Color(0xFF1565C0)
        Category.SPORTS -> Color(0xFF2E7D32)
        Category.CARS -> Color(0xFF6A1B9A)
        Category.FOOD -> Color(0xFFE65100)
        Category.SCIENCE -> Color(0xFF006064)
        Category.SPACE -> Color(0xFF4527A0)
        Category.ANIMALS -> Color(0xFF558B2F)
        Category.MUSIC -> Color(0xFFC2185B)
        Category.HISTORY -> Color(0xFF4E342E)
        Category.MATH -> Color(0xFF37474F)
        Category.HEALTH -> Color(0xFFC62828)
        Category.WEATHER -> Color(0xFF0277BD)
        Category.EMOTIONS -> Color(0xFFF57F17)
    }
}
