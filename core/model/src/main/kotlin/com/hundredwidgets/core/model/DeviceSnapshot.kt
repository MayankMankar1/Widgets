package com.hundredwidgets.core.model

import kotlinx.serialization.Serializable

/** Last known value of every data source. Widgets render from this, never from live calls. */
@Serializable
data class DeviceSnapshot(
    val battery: BatterySnap? = null,
    val updatedAt: Map<DataSource, Long> = emptyMap(),
)

@Serializable
data class BatterySnap(
    val level: Int,
    val isCharging: Boolean,
)
