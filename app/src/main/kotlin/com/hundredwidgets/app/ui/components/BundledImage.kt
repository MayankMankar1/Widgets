package com.hundredwidgets.app.ui.components

import android.content.Context
import android.graphics.BitmapFactory
import android.util.LruCache
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.hundredwidgets.app.ui.motion.LocalReducedMotion
import com.hundredwidgets.app.ui.motion.Motion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Decodes bundled bitmaps off the main thread and keeps recent ones in memory, so a list of cards
 * showing the same backdrop decodes it once. `painterResource` decodes on the main thread for
 * every card instead, which is what made scrolling stutter.
 */
object BundledImages {
    // ~48 MB: the six theme backdrops (~4.9 MB each) plus a screen or two of grid thumbnails
    private val cache = object : LruCache<Int, ImageBitmap>(48 * 1024 * 1024) {
        override fun sizeOf(key: Int, value: ImageBitmap) = value.width * value.height * 4
    }

    fun cached(@DrawableRes res: Int): ImageBitmap? = cache.get(res)

    suspend fun load(context: Context, @DrawableRes res: Int): ImageBitmap = cache.get(res)
        ?: withContext(Dispatchers.IO) {
            BitmapFactory.decodeResource(context.resources, res).asImageBitmap()
        }.also { cache.put(res, it) }
}

@Composable
fun rememberBundledImage(@DrawableRes res: Int): ImageBitmap? {
    val context = LocalContext.current
    val image by produceState(BundledImages.cached(res), res) { value = BundledImages.load(context, res) }
    return image
}

/**
 * A bundled image that fills its bounds. Until it's decoded the space shows a quiet surface
 * tone; an image that wasn't already in memory fades in.
 */
@Composable
fun BundledImage(
    @DrawableRes res: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
) {
    val wasCached = remember(res) { BundledImages.cached(res) != null }
    val image = rememberBundledImage(res)
    val reduced = LocalReducedMotion.current
    val alpha by animateFloatAsState(
        targetValue = if (image != null) 1f else 0f,
        animationSpec = if (wasCached || reduced) tween(0) else tween(Motion.MEDIUM),
        label = "bundledImage",
    )
    Box(modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh)) {
        if (image != null) {
            Image(
                bitmap = image,
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { this.alpha = alpha },
            )
        }
    }
}
