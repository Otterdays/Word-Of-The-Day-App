package com.example.wordofday.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.wordofday.data.model.Category
import com.example.wordofday.data.model.InterestGroup

// [TRACE: DOCS/ROADMAP.md] — §8b category color accents
object CategoryAccent {

    fun container(category: Category): Color = specific[category] ?: groupContainer(category.group)

    fun onContainer(category: Category): Color = specificOn[category] ?: groupOnContainer(category.group)

    private fun groupContainer(group: InterestGroup): Color = when (group) {
        InterestGroup.POPULAR -> Color(0xFFE8EAF6)
        InterestGroup.STEM -> Color(0xFFE3F2FD)
        InterestGroup.CREATIVE -> Color(0xFFFCE4EC)
        InterestGroup.LIFE -> Color(0xFFE8F5E9)
        InterestGroup.WORLD -> Color(0xFFEFEBE9)
        InterestGroup.ACADEMIC -> Color(0xFFFFF8E1)
    }

    private fun groupOnContainer(group: InterestGroup): Color = when (group) {
        InterestGroup.POPULAR -> Color(0xFF283593)
        InterestGroup.STEM -> Color(0xFF1565C0)
        InterestGroup.CREATIVE -> Color(0xFFC2185B)
        InterestGroup.LIFE -> Color(0xFF2E7D32)
        InterestGroup.WORLD -> Color(0xFF4E342E)
        InterestGroup.ACADEMIC -> Color(0xFFF57F17)
    }

    private val specific = mapOf(
        Category.MYTHOLOGY to Color(0xFFFFF3E0),
        Category.LITERATURE to Color(0xFFF3E5F5),
        Category.PHILOSOPHY to Color(0xFFE8EAF6),
        Category.SACRED to Color(0xFFFFFDE7),
        Category.TECH to Color(0xFFE3F2FD),
        Category.SPORTS to Color(0xFFE8F5E9),
        Category.FOOD to Color(0xFFFFF3E0),
        Category.SCIENCE to Color(0xFFE0F7FA),
        Category.SPACE to Color(0xFFEDE7F6),
        Category.GAMING to Color(0xFFEDE7F6),
    )

    private val specificOn = mapOf(
        Category.MYTHOLOGY to Color(0xFFE65100),
        Category.LITERATURE to Color(0xFF6A1B9A),
        Category.PHILOSOPHY to Color(0xFF283593),
        Category.SACRED to Color(0xFFF9A825),
        Category.TECH to Color(0xFF1565C0),
        Category.SPORTS to Color(0xFF2E7D32),
        Category.FOOD to Color(0xFFE65100),
        Category.SCIENCE to Color(0xFF006064),
        Category.SPACE to Color(0xFF4527A0),
        Category.GAMING to Color(0xFF4527A0),
    )
}
