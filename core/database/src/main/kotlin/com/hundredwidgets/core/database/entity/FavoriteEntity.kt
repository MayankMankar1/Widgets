package com.hundredwidgets.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    @ColumnInfo(name = "design_id") val designId: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
)
