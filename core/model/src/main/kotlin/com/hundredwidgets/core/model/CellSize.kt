package com.hundredwidgets.core.model

/** Launcher grid sizes. Each one has exactly one widget receiver. */
enum class CellSize(val columns: Int, val rows: Int) {
    S1x1(1, 1),
    S1x2(1, 2),
    S2x1(2, 1),
    S2x2(2, 2),
    S4x1(4, 1),
    S4x2(4, 2),
    ;

    val label: String get() = "$columns×$rows"

    companion object {
        fun fromName(name: String): CellSize? = entries.firstOrNull { it.name == name }
    }
}
