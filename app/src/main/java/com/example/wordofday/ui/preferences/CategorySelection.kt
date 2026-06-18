package com.example.wordofday.ui.preferences

import com.example.wordofday.data.model.Category

internal fun Set<Category>.toggleCategorySelection(cat: Category, max: Int = 8): Set<Category> {
    if (cat in this) {
        if (size <= 1) return this
        return this - cat
    }
    if (size >= max) return this
    return this + cat
}
