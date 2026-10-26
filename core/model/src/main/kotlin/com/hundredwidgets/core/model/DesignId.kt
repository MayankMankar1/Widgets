package com.hundredwidgets.core.model

import kotlinx.serialization.Serializable

/**
 * Permanent identifier of a design, `theme.snake_name` (e.g. `horizon.clock_quote`).
 * Stored on users' phones, so never rename or reuse one.
 */
@Serializable
@JvmInline
value class DesignId(val value: String) {
    val themeId: String get() = value.substringBefore('.')

    override fun toString(): String = value
}
