package com.hundredwidgets.core.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import com.hundredwidgets.core.model.BatterySnap
import com.hundredwidgets.core.model.DataSource
import com.hundredwidgets.core.model.DeviceSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

/** Last known device data. Collectors write it; widgets read it. */
interface DeviceSnapshotRepository {
    val snapshot: Flow<DeviceSnapshot>

    suspend fun current(): DeviceSnapshot

    /** Returns true when the stored value changed, so callers can skip needless redraws. */
    suspend fun updateBattery(battery: BatterySnap): Boolean
}

internal class DataStoreDeviceSnapshotRepository @Inject constructor(
    private val store: DataStore<DeviceSnapshot>,
) : DeviceSnapshotRepository {

    override val snapshot: Flow<DeviceSnapshot> = store.data

    override suspend fun current(): DeviceSnapshot = store.data.first()

    override suspend fun updateBattery(battery: BatterySnap): Boolean {
        var changed = false
        store.updateData { old ->
            if (old.battery == battery) {
                old
            } else {
                changed = true
                old.copy(
                    battery = battery,
                    updatedAt = old.updatedAt + (DataSource.BATTERY to System.currentTimeMillis()),
                )
            }
        }
        return changed
    }
}

internal object DeviceSnapshotSerializer : Serializer<DeviceSnapshot> {
    private val json = Json { ignoreUnknownKeys = true }

    override val defaultValue: DeviceSnapshot = DeviceSnapshot()

    override suspend fun readFrom(input: InputStream): DeviceSnapshot =
        try {
            json.decodeFromString(DeviceSnapshot.serializer(), input.readBytes().decodeToString())
        } catch (e: SerializationException) {
            throw CorruptionException("Unreadable device snapshot", e)
        }

    override suspend fun writeTo(t: DeviceSnapshot, output: OutputStream) {
        output.write(json.encodeToString(DeviceSnapshot.serializer(), t).encodeToByteArray())
    }
}
