package com.hundredwidgets.core.data

import com.hundredwidgets.core.database.dao.WidgetInstanceDao
import com.hundredwidgets.core.database.entity.WidgetInstanceEntity
import com.hundredwidgets.core.model.CellSize
import com.hundredwidgets.core.model.DesignId
import com.hundredwidgets.core.model.InstanceConfig
import com.hundredwidgets.core.model.WidgetInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Placed widgets: which design each appWidgetId shows, and its options. */
interface WidgetInstanceRepository {
    val instances: Flow<List<WidgetInstance>>

    fun observe(appWidgetId: Int): Flow<WidgetInstance?>

    suspend fun get(appWidgetId: Int): WidgetInstance?

    suspend fun getAll(): List<WidgetInstance>

    suspend fun save(
        appWidgetId: Int,
        designId: DesignId,
        size: CellSize,
        config: InstanceConfig = InstanceConfig(),
    )

    suspend fun delete(appWidgetIds: List<Int>)

    /** Launchers give restored widgets new IDs; keep their designs (README §12.3). */
    suspend fun remap(oldIds: IntArray, newIds: IntArray)

    /** Drops rows for widgets that no longer exist on any home screen. */
    suspend fun keepOnly(validIds: List<Int>)
}

internal class RoomWidgetInstanceRepository @Inject constructor(
    private val dao: WidgetInstanceDao,
) : WidgetInstanceRepository {

    override val instances: Flow<List<WidgetInstance>> =
        dao.observeAll().map { rows -> rows.mapNotNull { it.toModel() } }

    override fun observe(appWidgetId: Int): Flow<WidgetInstance?> =
        dao.observe(appWidgetId).map { it?.toModel() }

    override suspend fun get(appWidgetId: Int): WidgetInstance? = dao.get(appWidgetId)?.toModel()

    override suspend fun getAll(): List<WidgetInstance> = dao.getAll().mapNotNull { it.toModel() }

    override suspend fun save(
        appWidgetId: Int,
        designId: DesignId,
        size: CellSize,
        config: InstanceConfig,
    ) {
        val now = System.currentTimeMillis()
        val createdAt = dao.get(appWidgetId)?.createdAt ?: now
        dao.upsert(
            WidgetInstanceEntity(
                appWidgetId = appWidgetId,
                designId = designId.value,
                size = size.name,
                configJson = config.toJson(),
                createdAt = createdAt,
                updatedAt = now,
            ),
        )
    }

    override suspend fun delete(appWidgetIds: List<Int>) = dao.delete(appWidgetIds)

    override suspend fun remap(oldIds: IntArray, newIds: IntArray) {
        oldIds.zip(newIds).forEach { (oldId, newId) -> dao.remap(oldId, newId) }
    }

    override suspend fun keepOnly(validIds: List<Int>) = dao.deleteAllExcept(validIds)
}

private fun WidgetInstanceEntity.toModel(): WidgetInstance? {
    val cellSize = CellSize.fromName(size) ?: return null
    return WidgetInstance(
        appWidgetId = appWidgetId,
        designId = DesignId(designId),
        size = cellSize,
        config = InstanceConfig.fromJson(configJson),
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}
