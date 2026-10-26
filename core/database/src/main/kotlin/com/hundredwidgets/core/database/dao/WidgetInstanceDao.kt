package com.hundredwidgets.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.hundredwidgets.core.database.entity.WidgetInstanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WidgetInstanceDao {

    @Query("SELECT * FROM widget_instances ORDER BY created_at")
    fun observeAll(): Flow<List<WidgetInstanceEntity>>

    @Query("SELECT * FROM widget_instances")
    suspend fun getAll(): List<WidgetInstanceEntity>

    @Query("SELECT * FROM widget_instances WHERE app_widget_id = :appWidgetId")
    fun observe(appWidgetId: Int): Flow<WidgetInstanceEntity?>

    @Query("SELECT * FROM widget_instances WHERE app_widget_id = :appWidgetId")
    suspend fun get(appWidgetId: Int): WidgetInstanceEntity?

    @Upsert
    suspend fun upsert(entity: WidgetInstanceEntity)

    @Query("DELETE FROM widget_instances WHERE app_widget_id IN (:appWidgetIds)")
    suspend fun delete(appWidgetIds: List<Int>)

    @Query("DELETE FROM widget_instances WHERE app_widget_id NOT IN (:validIds)")
    suspend fun deleteAllExcept(validIds: List<Int>)

    @Query("UPDATE widget_instances SET app_widget_id = :newId WHERE app_widget_id = :oldId")
    suspend fun remap(oldId: Int, newId: Int)
}
