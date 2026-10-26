package com.hundredwidgets.widget.designs

import com.hundredwidgets.core.model.CellSize
import com.hundredwidgets.core.model.DesignId
import com.hundredwidgets.core.model.Theme
import com.hundredwidgets.widget.designs.bauhaus.bauhausDesigns
import com.hundredwidgets.widget.designs.comic.comicDesigns
import com.hundredwidgets.widget.designs.horizon.horizonDesigns
import com.hundredwidgets.widget.designs.noir.noirDesigns

/** Every design in the app. Adding a design never needs a new manifest receiver. */
object WidgetRegistry {

    val all: List<WidgetDesign> = horizonDesigns + noirDesigns + bauhausDesigns + comicDesigns

    private val byId: Map<DesignId, WidgetDesign> = all.associateBy { it.id }

    fun find(id: DesignId): WidgetDesign? = byId[id]

    fun forSize(size: CellSize): List<WidgetDesign> = all.filter { it.size == size }

    fun forTheme(theme: Theme): List<WidgetDesign> = all.filter { it.theme == theme }
}
