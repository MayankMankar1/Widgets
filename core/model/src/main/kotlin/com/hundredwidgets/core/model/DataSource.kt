package com.hundredwidgets.core.model

/**
 * Something a design depends on that changes over time. When a source changes, only the
 * widgets whose design needs it are redrawn. Live clock text needs no source: TextClock and
 * AnalogClock tick in the launcher.
 */
enum class DataSource(val refreshedPeriodically: Boolean) {
    TIME_OF_DAY(refreshedPeriodically = false),
    DATE(refreshedPeriodically = false),
    BATTERY(refreshedPeriodically = true),
    CALENDAR(refreshedPeriodically = false),
    STEPS(refreshedPeriodically = true),
    NEXT_ALARM(refreshedPeriodically = false),
    DND(refreshedPeriodically = true),
    LOCAL(refreshedPeriodically = false),
    // WEATHER and MEDIA come later (README §2).
}
