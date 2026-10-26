package com.hundredwidgets.core.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.hundredwidgets.core.data.DataStoreDeviceSnapshotRepository
import com.hundredwidgets.core.data.DataStoreSettingsRepository
import com.hundredwidgets.core.data.DeviceSnapshotRepository
import com.hundredwidgets.core.data.DeviceSnapshotSerializer
import com.hundredwidgets.core.data.EntitlementRepository
import com.hundredwidgets.core.data.FavoritesRepository
import com.hundredwidgets.core.data.FreeEntitlementRepository
import com.hundredwidgets.core.data.RoomFavoritesRepository
import com.hundredwidgets.core.data.RoomWidgetInstanceRepository
import com.hundredwidgets.core.data.SettingsRepository
import com.hundredwidgets.core.data.WidgetInstanceRepository
import com.hundredwidgets.core.database.HundredWidgetsDatabase
import com.hundredwidgets.core.database.dao.FavoriteDao
import com.hundredwidgets.core.database.dao.WidgetInstanceDao
import com.hundredwidgets.core.model.DeviceSnapshot
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds
    abstract fun bindWidgetInstances(impl: RoomWidgetInstanceRepository): WidgetInstanceRepository

    @Binds
    abstract fun bindFavorites(impl: RoomFavoritesRepository): FavoritesRepository

    @Binds
    abstract fun bindDeviceSnapshots(impl: DataStoreDeviceSnapshotRepository): DeviceSnapshotRepository

    @Binds
    abstract fun bindSettings(impl: DataStoreSettingsRepository): SettingsRepository

    @Binds
    abstract fun bindEntitlement(impl: FreeEntitlementRepository): EntitlementRepository

    companion object {

        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext context: Context): HundredWidgetsDatabase =
            Room.databaseBuilder(context, HundredWidgetsDatabase::class.java, HundredWidgetsDatabase.NAME)
                .build()

        @Provides
        fun provideWidgetInstanceDao(db: HundredWidgetsDatabase): WidgetInstanceDao = db.widgetInstanceDao()

        @Provides
        fun provideFavoriteDao(db: HundredWidgetsDatabase): FavoriteDao = db.favoriteDao()

        @Provides
        @Singleton
        fun provideDeviceSnapshotStore(@ApplicationContext context: Context): DataStore<DeviceSnapshot> =
            DataStoreFactory.create(
                serializer = DeviceSnapshotSerializer,
                corruptionHandler = ReplaceFileCorruptionHandler { DeviceSnapshot() },
                produceFile = { context.dataStoreFile("device_snapshot.json") },
            )

        @Provides
        @Singleton
        fun provideSettingsStore(@ApplicationContext context: Context): DataStore<Preferences> =
            PreferenceDataStoreFactory.create(
                produceFile = { context.preferencesDataStoreFile("settings") },
            )
    }
}
