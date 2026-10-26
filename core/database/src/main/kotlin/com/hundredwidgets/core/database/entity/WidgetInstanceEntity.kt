package com.hundredwidgets.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "widget_instances",
    indices = [Index("design_id")],
)
data class WidgetInstanceEntity(
    @PrimaryKey
    @ColumnInfo(name = "app_widget_id") val appWidgetId: Int,
    @ColumnInfo(name = "design_id") val designId: String,
    @ColumnInfo(name = "size") val size: String,
    @ColumnInfo(name = "config_json", defaultValue = "{}") val configJson: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
)
