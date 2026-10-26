package com.hundredwidgets.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.hundredwidgets.core.model.ClockFormat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

const val DEFAULT_REFRESH_MINUTES = 30
val REFRESH_INTERVAL_OPTIONS = listOf(15, 30, 60)

interface SettingsRepository {
    val clockFormat: Flow<ClockFormat>
    val refreshIntervalMinutes: Flow<Int>

    suspend fun setClockFormat(format: ClockFormat)

    suspend fun setRefreshIntervalMinutes(minutes: Int)
}

internal class DataStoreSettingsRepository @Inject constructor(
    private val prefs: DataStore<Preferences>,
) : SettingsRepository {

    override val clockFormat: Flow<ClockFormat> = prefs.data.map { p ->
        p[CLOCK_FORMAT]?.let { name -> ClockFormat.entries.firstOrNull { it.name == name } }
            ?: ClockFormat.SYSTEM
    }

    override val refreshIntervalMinutes: Flow<Int> = prefs.data.map { p ->
        p[REFRESH_INTERVAL] ?: DEFAULT_REFRESH_MINUTES
    }

    override suspend fun setClockFormat(format: ClockFormat) {
        prefs.edit { it[CLOCK_FORMAT] = format.name }
    }

    override suspend fun setRefreshIntervalMinutes(minutes: Int) {
        // WorkManager's floor is 15 minutes.
        prefs.edit { it[REFRESH_INTERVAL] = minutes.coerceIn(15, 60) }
    }

    private companion object {
        val CLOCK_FORMAT = stringPreferencesKey("clock_format")
        val REFRESH_INTERVAL = intPreferencesKey("refresh_interval_min")
    }
}
