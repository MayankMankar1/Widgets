package com.hundredwidgets.widget.designs.common

import com.hundredwidgets.core.model.BatterySnap
import com.hundredwidgets.widget.designs.WidgetRenderContext
import java.time.DayOfWeek
import java.time.LocalDate

/** Stand-in shown only in the in-app catalogue when no reading exists yet. */
internal val SampleBattery = BatterySnap(level = 78, isCharging = false)

/** The real battery reading, a sample in previews, or null (designs then show "--%"). */
internal fun WidgetRenderContext.batteryOrSample(): BatterySnap? =
    snapshot.battery ?: SampleBattery.takeIf { isPreview }

/**
 * Date-derived text that TextClock can't produce. It is computed when the widget is drawn,
 * so designs using it declare DataSource.DATE to be redrawn when the date changes.
 */
internal object DesignDates {
    fun dayOfYear(today: LocalDate = LocalDate.now()): Int = today.dayOfYear

    /** 0 on Saturday and Sunday, otherwise the days left until Saturday. */
    fun daysToSaturday(today: LocalDate = LocalDate.now()): Int = when (today.dayOfWeek) {
        DayOfWeek.SATURDAY, DayOfWeek.SUNDAY -> 0
        else -> DayOfWeek.SATURDAY.value - today.dayOfWeek.value
    }
}
