package com.hundredwidgets.app.ui.wallpapers

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hundredwidgets.app.R
import com.hundredwidgets.core.model.Theme

/** Where a wallpaper comes from; it names numbered wallpapers ("Glass 3"). */
enum class WallpaperCollection(@StringRes val title: Int) {
    GLASS(R.string.wallpapers_collection_glass),
    LANDSCAPES(R.string.wallpapers_collection_landscapes),
    CLAY(R.string.wallpapers_collection_clay),
    CLAY_NIGHT(R.string.wallpapers_collection_clay_night),
    SCENES(R.string.wallpapers_collection_scenes),
}

/**
 * One bundled wallpaper. The grid shows the small [thumb]; only the preview and "Set" read the
 * full-size [image], so a long list of wallpapers stays light on memory.
 */
data class Wallpaper(
    val id: String,
    val collection: WallpaperCollection,
    @DrawableRes val image: Int,
    @DrawableRes val thumb: Int,
    @StringRes val name: Int? = null,
    val number: Int = 0,
    val theme: Theme? = null,
)

/** The name read out for a tile and shown in the preview: its own name, its theme's, or "Glass 3". */
@Composable
fun Wallpaper.title(): String = when {
    theme != null -> theme.displayName
    name != null -> stringResource(name)
    else -> stringResource(R.string.wallpapers_item_title, stringResource(collection.title), number)
}

/**
 * Every wallpaper the app ships. Images are made by `tools/wallpapers` (README there) and live in
 * `drawable-nodpi` as `wp_*` (full size) and `wpt_*` (thumbnail). Online wallpapers come later.
 */
object WallpaperCatalog {
    private fun numbered(collection: WallpaperCollection, prefix: String, images: List<Pair<Int, Int>>) =
        images.mapIndexed { i, (image, thumb) ->
            Wallpaper("${prefix}_%02d".format(i + 1), collection, image, thumb, number = i + 1)
        }

    private val glass = numbered(
        WallpaperCollection.GLASS, "glass",
        listOf(
            R.drawable.wp_glass_01 to R.drawable.wpt_glass_01,
            R.drawable.wp_glass_02 to R.drawable.wpt_glass_02,
            R.drawable.wp_glass_03 to R.drawable.wpt_glass_03,
            R.drawable.wp_glass_04 to R.drawable.wpt_glass_04,
            R.drawable.wp_glass_05 to R.drawable.wpt_glass_05,
            R.drawable.wp_glass_06 to R.drawable.wpt_glass_06,
        ),
    )

    private val landscapes = listOf(
        scape("scape_cherry_lake", R.drawable.wp_scape_cherry_lake, R.drawable.wpt_scape_cherry_lake, R.string.wallpaper_cherry_lake),
        scape("scape_floating_castle", R.drawable.wp_scape_floating_castle, R.drawable.wpt_scape_floating_castle, R.string.wallpaper_floating_castle),
        scape("scape_autumn_road", R.drawable.wp_scape_autumn_road, R.drawable.wpt_scape_autumn_road, R.string.wallpaper_autumn_road),
        scape("scape_moonlit_dunes", R.drawable.wp_scape_moonlit_dunes, R.drawable.wpt_scape_moonlit_dunes, R.string.wallpaper_moonlit_dunes),
        scape("scape_concrete_sunset", R.drawable.wp_scape_concrete_sunset, R.drawable.wpt_scape_concrete_sunset, R.string.wallpaper_concrete_sunset),
        scape("scape_red_valley", R.drawable.wp_scape_red_valley, R.drawable.wpt_scape_red_valley, R.string.wallpaper_red_valley),
    )

    private val clay = numbered(
        WallpaperCollection.CLAY, "clay",
        listOf(
            R.drawable.wp_clay_01 to R.drawable.wpt_clay_01,
            R.drawable.wp_clay_02 to R.drawable.wpt_clay_02,
            R.drawable.wp_clay_03 to R.drawable.wpt_clay_03,
            R.drawable.wp_clay_04 to R.drawable.wpt_clay_04,
            R.drawable.wp_clay_05 to R.drawable.wpt_clay_05,
            R.drawable.wp_clay_06 to R.drawable.wpt_clay_06,
        ),
    )

    private val clayNight = numbered(
        WallpaperCollection.CLAY_NIGHT, "clay_night",
        listOf(
            R.drawable.wp_clay_night_01 to R.drawable.wpt_clay_night_01,
            R.drawable.wp_clay_night_02 to R.drawable.wpt_clay_night_02,
            R.drawable.wp_clay_night_03 to R.drawable.wpt_clay_night_03,
            R.drawable.wp_clay_night_04 to R.drawable.wpt_clay_night_04,
            R.drawable.wp_clay_night_05 to R.drawable.wpt_clay_night_05,
            R.drawable.wp_clay_night_06 to R.drawable.wpt_clay_night_06,
        ),
    )

    // Only Comic's theme wallpaper is offered here; the other theme wallpapers stay as scene
    // backdrops on Home and behind widget previews.
    private val comic = Wallpaper(
        "theme_comic", WallpaperCollection.SCENES, R.drawable.wp_comic, R.drawable.wpt_comic, theme = Theme.COMIC,
    )

    /** Grid order: one from each collection in turn, so neighbouring tiles look different. */
    val all: List<Wallpaper> = buildList {
        val sources = listOf(glass, landscapes, clay, clayNight)
        for (i in 0 until sources.maxOf { it.size }) {
            sources.forEach { source -> source.getOrNull(i)?.let(::add) }
            if (i == 2) add(comic)
        }
    }

    fun find(id: String): Wallpaper? = all.firstOrNull { it.id == id }

    private fun scape(id: String, image: Int, thumb: Int, name: Int) =
        Wallpaper(id, WallpaperCollection.LANDSCAPES, image, thumb, name = name)
}
