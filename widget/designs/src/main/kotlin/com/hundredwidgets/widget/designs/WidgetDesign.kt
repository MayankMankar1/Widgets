package com.hundredwidgets.widget.designs

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import com.hundredwidgets.core.model.AppPermission
import com.hundredwidgets.core.model.Category
import com.hundredwidgets.core.model.CellSize
import com.hundredwidgets.core.model.ClockFormat
import com.hundredwidgets.core.model.DataSource
import com.hundredwidgets.core.model.DesignId
import com.hundredwidgets.core.model.DeviceSnapshot
import com.hundredwidgets.core.model.InstanceConfig
import com.hundredwidgets.core.model.Theme
import com.hundredwidgets.core.model.Tier

/** One widget the user can add. Its [content] is Glance-only: it becomes RemoteViews. */
data class WidgetDesign(
    val id: DesignId,
    val theme: Theme,
    val category: Category,
    val size: CellSize,
    @StringRes val title: Int,
    val needs: Set<DataSource> = emptySet(),
    val permissions: Set<AppPermission> = emptySet(),
    val tier: Tier = Tier.FREE,
    val configurable: Boolean = false,
    /** versionCode the design shipped in; drives "Newly added". */
    val addedIn: Int,
    val content: @Composable (WidgetRenderContext) -> Unit,
)

data class WidgetRenderContext(
    val config: InstanceConfig,
    /** Already resolved: the instance override, else the app setting. */
    val clockFormat: ClockFormat,
    val snapshot: DeviceSnapshot,
    /** True in the in-app catalogue, where sample data stands in for missing values. */
    val isPreview: Boolean,
)
