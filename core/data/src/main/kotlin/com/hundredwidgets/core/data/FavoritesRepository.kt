package com.hundredwidgets.core.data

import com.hundredwidgets.core.database.dao.FavoriteDao
import com.hundredwidgets.core.database.entity.FavoriteEntity
import com.hundredwidgets.core.model.DesignId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface FavoritesRepository {
    val favorites: Flow<Set<DesignId>>

    suspend fun toggle(designId: DesignId)
}

internal class RoomFavoritesRepository @Inject constructor(
    private val dao: FavoriteDao,
) : FavoritesRepository {

    override val favorites: Flow<Set<DesignId>> =
        dao.observeIds().map { ids -> ids.map(::DesignId).toSet() }

    override suspend fun toggle(designId: DesignId) {
        if (dao.contains(designId.value)) {
            dao.delete(designId.value)
        } else {
            dao.insert(FavoriteEntity(designId = designId.value, createdAt = System.currentTimeMillis()))
        }
    }
}
