package com.hundredwidgets.widget.designs.comic

import android.app.SearchManager
import android.content.Intent
import android.view.Gravity
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.unit.ColorProvider
import com.hundredwidgets.widget.designs.R
import com.hundredwidgets.widget.designs.WidgetRenderContext
import com.hundredwidgets.widget.designs.common.GlanceStyledText
import com.hundredwidgets.widget.designs.common.WidgetFont

/** Canvas C2 "Search": an inked search bar with a GO! burst; tapping opens web search. */
@Composable
internal fun ComicSearch(ctx: WidgetRenderContext) {
    val context = LocalContext.current
    val search = Intent(Intent.ACTION_WEB_SEARCH)
        .putExtra(SearchManager.QUERY, "")
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    val tap = if (ctx.isPreview) GlanceModifier else GlanceModifier.clickable(actionStartActivity(search))
    Row(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(R.drawable.wd_bg_comic_panel))
            .then(tap)
            .padding(start = 18.dp, end = 12.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            provider = ImageProvider(R.drawable.wd_ic_search),
            contentDescription = null,
            colorFilter = ColorFilter.tint(ColorProvider(ComicColors.ink)),
            modifier = GlanceModifier.size(24.dp),
        )
        Spacer(GlanceModifier.width(10.dp))
        GlanceStyledText(
            text = context.getString(R.string.wd_comic_search_hint),
            font = WidgetFont.COMIC_NEUE_BOLD,
            sizeSp = 15f,
            color = ComicColors.ink,
            modifier = GlanceModifier.defaultWeight(),
        )
        Box(
            modifier = GlanceModifier
                .size(width = 64.dp, height = 50.dp)
                .background(ImageProvider(R.drawable.wd_comic_burst)),
            contentAlignment = Alignment.Center,
        ) {
            GlanceStyledText(
                text = context.getString(R.string.wd_comic_go),
                font = WidgetFont.BANGERS,
                sizeSp = 17f,
                color = ComicColors.cream,
                gravity = Gravity.CENTER,
            )
        }
    }
}
