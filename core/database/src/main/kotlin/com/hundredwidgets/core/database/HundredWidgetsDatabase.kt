package com.hundredwidgets.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hundredwidgets.core.database.dao.FavoriteDao
import com.hundredwidgets.core.database.dao.WidgetInstanceDao
import com.hundredwidgets.core.database.entity.FavoriteEntity
import com.hundredwidgets.core.database.entity.WidgetInstanceEntity

/**
 * Version 1 may still change freely until the first Play release. After that, every schema
 * change needs a version bump, a Migration and a migration test (CLAUDE.md).
 */
@Database(
    entities = [
        WidgetInstanceEntity::class,
        FavoriteEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class HundredWidgetsDatabase : RoomDatabase() {
    abstract fun widgetInstanceDao(): WidgetInstanceDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        const val NAME = "hundredwidgets.db"
    }
}
