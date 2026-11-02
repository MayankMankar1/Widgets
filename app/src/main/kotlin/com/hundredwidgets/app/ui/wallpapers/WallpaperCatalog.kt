package com.hundredwidgets.app.ui.wallpapers

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hundredwidgets.app.R
import com.hundredwidgets.app.ui.info
import com.hundredwidgets.core.model.Theme

/** Wallpaper collections in the order the Wallpapers tab shows them. */
enum class WallpaperCollection(@StringRes val title: Int, @StringRes val tagline: Int) {
    GLASS(R.string.wallpapers_collection_glass, R.string.wallpapers_collection_glass_tagline),
    LANDSCAPES(R.string.wallpapers_collection_landscapes, R.string.wallpapers_collection_landscapes_tagline),
    CLAY(R.string.wallpapers_collection_clay, R.string.wallpapers_collection_clay_tagline),
    CLAY_NIGHT(R.string.wallpapers_collection_clay_night, R.string.wallpapers_collection_clay_night_tagline),
    SCENES(R.string.wallpapers_collection_scenes, R.string.wallpapers_collection_scenes_tagline),
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

/** The name shown under a tile and in the preview: its own name, its theme's, or "Glass 3". */
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

    val all: List<Wallpaper> = buildList {
        addAll(
            numbered(
                WallpaperCollection.GLASS, "glass",
                listOf(
                    R.drawable.wp_glass_01 to R.drawable.wpt_glass_01,
                    R.drawable.wp_glass_02 to R.drawable.wpt_glass_02,
                    R.drawable.wp_glass_03 to R.drawable.wpt_glass_03,
                    R.drawable.wp_glass_04 to R.drawable.wpt_glass_04,
                    R.drawable.wp_glass_05 to R.drawable.wpt_glass_05,
                    R.drawable.wp_glass_06 to R.drawable.wpt_glass_06,
                ),
            ),
        )
        add(scape("scape_cherry_lake", R.drawable.wp_scape_cherry_lake, R.drawable.wpt_scape_cherry_lake, R.string.wallpaper_cherry_lake))
        add(scape("scape_floating_castle", R.drawable.wp_scape_floating_castle, R.drawable.wpt_scape_floating_castle, R.string.wallpaper_floating_castle))
        add(scape("scape_autumn_road", R.drawable.wp_scape_autumn_road, R.drawable.wpt_scape_autumn_road, R.string.wallpaper_autumn_road))
        add(scape("scape_moonlit_dunes", R.drawable.wp_scape_moonlit_dunes, R.drawable.wpt_scape_moonlit_dunes, R.string.wallpaper_moonlit_dunes))
        add(scape("scape_concrete_sunset", R.drawable.wp_scape_concrete_sunset, R.drawable.wpt_scape_concrete_sunset, R.string.wallpaper_concrete_sunset))
        add(scape("scape_red_valley", R.drawable.wp_scape_red_valley, R.drawable.wpt_scape_red_valley, R.string.wallpaper_red_valley))
        addAll(
            numbered(
                WallpaperCollection.CLAY, "clay",
                listOf(
                    R.drawable.wp_clay_01 to R.drawable.wpt_clay_01,
                    R.drawable.wp_clay_02 to R.drawable.wpt_clay_02,
                    R.drawable.wp_clay_03 to R.drawable.wpt_clay_03,
                    R.drawable.wp_clay_04 to R.drawable.wpt_clay_04,
                    R.drawable.wp_clay_05 to R.drawable.wpt_clay_05,
                    R.drawable.wp_clay_06 to R.drawable.wpt_clay_06,
                ),
            ),
        )
        addAll(
            numbered(
                WallpaperCollection.CLAY_NIGHT, "clay_night",
                listOf(
                    R.drawable.wp_clay_night_01 to R.drawable.wpt_clay_night_01,
                    R.drawable.wp_clay_night_02 to R.drawable.wpt_clay_night_02,
                    R.drawable.wp_clay_night_03 to R.drawable.wpt_clay_night_03,
                    R.drawable.wp_clay_night_04 to R.drawable.wpt_clay_night_04,
                    R.drawable.wp_clay_night_05 to R.drawable.wpt_clay_night_05,
                    R.drawable.wp_clay_night_06 to R.drawable.wpt_clay_night_06,
                ),
            ),
        )
        addAll(Theme.entries.map { theme -> themeWallpaper(theme) })
    }

    val byCollection: Map<WallpaperCollection, List<Wallpaper>> = all.groupBy { it.collection }

    fun find(id: String): Wallpaper? = all.firstOrNull { it.id == id }

    private fun scape(id: String, image: Int, thumb: Int, name: Int) =
        Wallpaper(id, WallpaperCollection.LANDSCAPES, image, thumb, name = name)

    private fun themeWallpaper(theme: Theme): Wallpaper {
        val thumb = when (theme) {
            Theme.HORIZON -> R.drawable.wpt_horizon
            Theme.NOIR -> R.drawable.wpt_noir
            Theme.PAPER -> R.drawable.wpt_paper
            Theme.SIGNAL -> R.drawable.wpt_signal
            Theme.BAUHAUS -> R.drawable.wpt_bauhaus
            Theme.COMIC -> R.drawable.wpt_comic
        }
        return Wallpaper("theme_${theme.id}", WallpaperCollection.SCENES, theme.info.wallpaper, thumb, theme = theme)
    }
}
