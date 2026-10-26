package com.hundredwidgets.widget.designs

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class WidgetRegistryTest {

    private val idPattern = Regex("[a-z]+\\.[a-z0-9_]+")

    @Test
    fun idsAreUnique() {
        val ids = WidgetRegistry.all.map { it.id }

        assertEquals("Duplicate design IDs", ids.size, ids.toSet().size)
    }

    @Test
    fun idsUseThemeDotSnakeCase() {
        WidgetRegistry.all.forEach { design ->
            assertTrue("${design.id} must look like theme.snake_name", idPattern.matches(design.id.value))
        }
    }

    @Test
    fun idsStartWithTheirTheme() {
        WidgetRegistry.all.forEach { design ->
            assertEquals("${design.id} theme prefix", design.theme.id, design.id.themeId)
        }
    }

    @Test
    fun findReturnsEveryDesign() {
        WidgetRegistry.all.forEach { design ->
            assertSame(design, WidgetRegistry.find(design.id))
        }
    }

    @Test
    fun addedInIsAPositiveVersionCode() {
        WidgetRegistry.all.forEach { design ->
            assertTrue("${design.id} addedIn", design.addedIn >= 1)
        }
    }
}
