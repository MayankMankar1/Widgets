package com.hundredwidgets.widget.data

import android.content.Context
import android.os.BatteryManager
import com.hundredwidgets.core.data.DeviceSnapshotRepository
import com.hundredwidgets.core.model.BatterySnap
import com.hundredwidgets.core.model.DataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Inject

/** Reads one data source and writes it to the snapshot. */
interface Collector {
    val source: DataSource

    /** Returns true when the snapshot changed, so the worker redraws affected widgets. */
    suspend fun collect(): Boolean
}

class BatteryCollector @Inject constructor(
    @ApplicationContext private val context: Context,
    private val snapshots: DeviceSnapshotRepository,
) : Collector {

    override val source: DataSource = DataSource.BATTERY

    override suspend fun collect(): Boolean {
        val manager = context.getSystemService(BatteryManager::class.java) ?: return false
        val level = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        if (level !in 0..100) return false
        return snapshots.updateBattery(BatterySnap(level = level, isCharging = manager.isCharging))
    }
}

@Module
@InstallIn(SingletonComponent::class)
internal abstract class CollectorModule {

    @Binds
    @IntoSet
    abstract fun bindBattery(impl: BatteryCollector): Collector
}
