package com.hundredwidgets.widget.engine

import com.hundredwidgets.core.data.DeviceSnapshotRepository
import com.hundredwidgets.core.data.SettingsRepository
import com.hundredwidgets.core.data.WidgetInstanceRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Glance widgets and receivers can't be constructor-injected, so they pull dependencies here. */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEngineEntryPoint {
    fun widgetInstances(): WidgetInstanceRepository
    fun deviceSnapshots(): DeviceSnapshotRepository
    fun settings(): SettingsRepository
    fun widgetUpdater(): WidgetUpdater
}
