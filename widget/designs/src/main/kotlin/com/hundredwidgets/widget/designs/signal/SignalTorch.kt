package com.hundredwidgets.widget.designs.signal

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.background
import androidx.glance.layout.Alignment
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

/**
 * Canvas S1 "Torch": a white pill that turns black while the torch is on. Uses
 * CameraManager.setTorchMode, which needs no camera permission (CLAUDE.md: never add CAMERA).
 */
@Composable
internal fun SignalTorch(ctx: WidgetRenderContext) {
    val context = LocalContext.current
    var on by remember { mutableStateOf(false) }
    val tap = if (ctx.isPreview) {
        GlanceModifier
    } else {
        GlanceModifier.clickable { if (setTorch(context, !on)) on = !on }
    }
    val ink = if (on) SignalColors.paper else SignalColors.ink
    Row(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(if (on) R.drawable.wd_bg_signal_pill_black else R.drawable.wd_bg_signal_pill_white))
            .then(tap)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            provider = ImageProvider(R.drawable.wd_ic_torch),
            contentDescription = null,
            colorFilter = ColorFilter.tint(ColorProvider(ink)),
            modifier = GlanceModifier.size(22.dp),
        )
        Spacer(GlanceModifier.width(12.dp))
        GlanceStyledText(
            text = context.getString(R.string.wd_signal_torch_label),
            font = WidgetFont.SPACE_MONO,
            sizeSp = 13f,
            color = ink,
        )
    }
}

/** Returns false when the phone has no flash or the camera is busy. */
private fun setTorch(context: Context, enabled: Boolean): Boolean = runCatching {
    val cameras = context.getSystemService(CameraManager::class.java) ?: return false
    val id = cameras.cameraIdList.firstOrNull { id ->
        cameras.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
    } ?: return false
    cameras.setTorchMode(id, enabled)
    true
}.getOrDefault(false)
