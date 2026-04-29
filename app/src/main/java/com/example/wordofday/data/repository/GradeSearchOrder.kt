package com.example.wordofday.data.repository

import com.example.wordofday.data.model.GradeLevel

/** Primary grade first, then ±1, ±2, … until all grades tried. */
internal fun gradeSearchOrder(primary: GradeLevel): List<GradeLevel> {
    val out = LinkedHashSet<GradeLevel>()
    out.add(primary)
    var spread = 1
    while (spread < GradeLevel.entries.size) {
        val lo = primary.ordinal - spread
        val hi = primary.ordinal + spread
        if (lo >= 0) out.add(GradeLevel.entries[lo])
        if (hi < GradeLevel.entries.size) out.add(GradeLevel.entries[hi])
        spread++
    }
    return out.toList()
}
