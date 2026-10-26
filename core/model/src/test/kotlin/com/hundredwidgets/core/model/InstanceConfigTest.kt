package com.hundredwidgets.core.model

import org.junit.Assert.assertEquals
import org.junit.Test

class InstanceConfigTest {

    @Test
    fun roundTripsThroughJson() {
        val config = InstanceConfig(clockFormat = ClockFormat.H24, appPackages = listOf("com.example.mail"))

        assertEquals(config, InstanceConfig.fromJson(config.toJson()))
    }

    @Test
    fun unreadableJsonFallsBackToDefaults() {
        assertEquals(InstanceConfig(), InstanceConfig.fromJson("not json"))
    }

    @Test
    fun unknownKeysFromNewerVersionsAreIgnored() {
        val raw = """{"todoListKey":"work","addedInAFutureVersion":true}"""

        assertEquals(InstanceConfig(todoListKey = "work"), InstanceConfig.fromJson(raw))
    }

    @Test
    fun designIdExposesItsTheme() {
        assertEquals("horizon", DesignId("horizon.clock_quote").themeId)
    }
}
