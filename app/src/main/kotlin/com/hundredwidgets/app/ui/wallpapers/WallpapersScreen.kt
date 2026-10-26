package com.hundredwidgets.app.ui.wallpapers

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hundredwidgets.app.R
import com.hundredwidgets.app.ui.components.ScreenTitle
import com.hundredwidgets.app.ui.info
import com.hundredwidgets.app.wallpaper.WallpaperTarget
import com.hundredwidgets.core.model.Theme

@Composable
fun WallpapersScreen(viewModel: WallpapersViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val status by viewModel.status.collectAsStateWithLifecycle()
    var picked by remember { mutableStateOf<Theme?>(null) }

    LaunchedEffect(status) {
        val message = when (status) {
            WallpaperStatus.DONE -> R.string.wallpapers_done
            WallpaperStatus.FAILED -> R.string.wallpapers_failed
            WallpaperStatus.APPLYING, null -> return@LaunchedEffect
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        viewModel.statusShown()
    }

    Column(Modifier.fillMaxSize()) {
        ScreenTitle(
            text = stringResource(R.string.tab_wallpapers),
            subtitle = stringResource(R.string.wallpapers_subtitle),
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(Theme.entries) { theme ->
                WallpaperTile(theme = theme, onClick = { picked = theme })
            }
        }
    }

    picked?.let { theme ->
        SetWallpaperDialog(
            theme = theme,
            onDismiss = { picked = null },
            onPick = { target ->
                viewModel.setWallpaper(theme, target)
                picked = null
            },
        )
    }
}

@Composable
private fun WallpaperTile(theme: Theme, onClick: () -> Unit) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(9f / 19.5f)
                .clip(RoundedCornerShape(20.dp))
                .clickable(onClick = onClick),
        ) {
            Image(
                painter = painterResource(theme.info.wallpaper),
                contentDescription = theme.displayName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
        Text(
            theme.displayName,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 6.dp, start = 4.dp),
        )
    }
}

@Composable
private fun SetWallpaperDialog(theme: Theme, onDismiss: () -> Unit, onPick: (WallpaperTarget) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.wallpapers_set_title, theme.displayName)) },
        text = {
            Column {
                TextButton(onClick = { onPick(WallpaperTarget.HOME) }) {
                    Text(stringResource(R.string.wallpapers_target_home))
                }
                TextButton(onClick = { onPick(WallpaperTarget.LOCK) }) {
                    Text(stringResource(R.string.wallpapers_target_lock))
                }
                TextButton(onClick = { onPick(WallpaperTarget.BOTH) }) {
                    Text(stringResource(R.string.wallpapers_target_both))
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        },
    )
}
