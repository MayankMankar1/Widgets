package com.hundredwidgets.app.configure

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.hundredwidgets.app.R
import com.hundredwidgets.app.ui.components.ScreenTitle
import com.hundredwidgets.app.ui.components.WidgetPreview
import com.hundredwidgets.app.ui.theme.HundredWidgetsTheme
import com.hundredwidgets.core.data.SettingsRepository
import com.hundredwidgets.core.data.WidgetInstanceRepository
import com.hundredwidgets.core.model.AppTheme
import com.hundredwidgets.core.model.CellSize
import com.hundredwidgets.core.model.ClockFormat
import com.hundredwidgets.widget.designs.WidgetDesign
import com.hundredwidgets.widget.designs.WidgetRegistry
import com.hundredwidgets.widget.engine.WidgetReceivers
import com.hundredwidgets.widget.engine.WidgetUpdater
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Design chooser for one placed widget. The launcher opens it when a widget is added from
 * the system picker (or reconfigured); an unconfigured widget opens it on tap.
 * Its class name is referenced by EngineContract.CONFIGURE_ACTIVITY and the widget info XML.
 */
@AndroidEntryPoint
class ConfigureActivity : ComponentActivity() {

    @Inject
    lateinit var instances: WidgetInstanceRepository

    @Inject
    lateinit var updater: WidgetUpdater

    @Inject
    lateinit var settings: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val appWidgetId = intent?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            ?: AppWidgetManager.INVALID_APPWIDGET_ID
        // Until a design is picked, backing out tells the launcher to remove the new widget.
        setResult(RESULT_CANCELED, resultIntent(appWidgetId))

        val providerClass = AppWidgetManager.getInstance(this).getAppWidgetInfo(appWidgetId)?.provider?.className
        val size = providerClass?.let(WidgetReceivers::sizeOf)
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID || size == null) {
            finish()
            return
        }

        setContent {
            val theme by settings.appTheme.collectAsStateWithLifecycle(AppTheme.SYSTEM)
            val dark = when (theme) {
                AppTheme.SYSTEM -> isSystemInDarkTheme()
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
            }
            HundredWidgetsTheme(darkTheme = dark) {
                val clockFormat by settings.clockFormat.collectAsStateWithLifecycle(ClockFormat.SYSTEM)
                ConfigureScreen(
                    size = size,
                    designs = WidgetRegistry.forSize(size),
                    clockFormat = clockFormat,
                    onPick = { design -> choose(appWidgetId, size, design) },
                    onCancel = ::finish,
                )
            }
        }
    }

    private fun choose(appWidgetId: Int, size: CellSize, design: WidgetDesign) {
        lifecycleScope.launch {
            instances.save(appWidgetId, design.id, size)
            updater.update(appWidgetId)
            setResult(RESULT_OK, resultIntent(appWidgetId))
            finish()
        }
    }

    private fun resultIntent(appWidgetId: Int): Intent =
        Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
}

@Composable
private fun ConfigureScreen(
    size: CellSize,
    designs: List<WidgetDesign>,
    clockFormat: ClockFormat,
    onPick: (WidgetDesign) -> Unit,
    onCancel: () -> Unit,
) {
    Surface(Modifier.fillMaxSize()) {
        Column(Modifier.safeDrawingPadding()) {
            ScreenTitle(
                text = stringResource(R.string.configure_title),
                subtitle = stringResource(R.string.configure_subtitle, size.label),
            )
            if (designs.isEmpty()) {
                Text(
                    stringResource(R.string.configure_empty),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(designs, key = { it.id.value }) { design ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPick(design) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            WidgetPreview(
                                design = design,
                                clockFormat = clockFormat,
                                scale = if (design.size.columns == 4) 0.75f else 0.85f,
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(stringResource(design.title), style = MaterialTheme.typography.titleSmall)
                            Text(
                                design.theme.displayName,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
            TextButton(onClick = onCancel, modifier = Modifier.padding(8.dp)) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    }
}
