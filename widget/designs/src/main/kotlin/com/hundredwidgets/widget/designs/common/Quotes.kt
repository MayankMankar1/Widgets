package com.hundredwidgets.widget.designs.common

import java.time.LocalDate

/** Bundled so quote widgets work offline. Lines come from the design canvas. */
internal object Quotes {
    private val all = listOf(
        "Discipline today builds freedom tomorrow.",
        "Keep going. You're closer than you think.",
        "One ridge at a time. The view is earned.",
        "Start small. Start today.",
        "Slow is smooth. Smooth becomes fast.",
    )

    fun ofTheDay(date: LocalDate = LocalDate.now()): String = all[(date.dayOfYear - 1) % all.size]
}
