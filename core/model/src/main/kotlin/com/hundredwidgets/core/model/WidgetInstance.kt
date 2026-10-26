package com.hundredwidgets.core.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/** One widget the user placed: Android's appWidgetId, the design it shows, and its options. */
data class WidgetInstance(
    val appWidgetId: Int,
    val designId: DesignId,
    val size: CellSize,
    val config: InstanceConfig,
    val createdAt: Long,
    val updatedAt: Long,
)

@Serializable
enum class ClockFormat { SYSTEM, H12, H24 }

/** Per-instance options, stored as JSON in `widget_instances.config_json`. */
@Serializable
data class InstanceConfig(
    /** Null follows the app-wide setting. */
    val clockFormat: ClockFormat? = null,
    val appPackages: List<String> = emptyList(),
    /** A copy inside app storage; the launcher can't read Photo Picker URIs. */
    val photoFile: String? = null,
    val todoListKey: String = "today",
) {
    fun toJson(): String = json.encodeToString(serializer(), this)

    companion object {
        private val json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = false
        }

        fun fromJson(raw: String): InstanceConfig =
            runCatching { json.decodeFromString(serializer(), raw) }.getOrDefault(InstanceConfig())
    }
}
