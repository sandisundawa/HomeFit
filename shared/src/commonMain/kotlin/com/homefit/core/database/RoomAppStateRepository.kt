package com.homefit.core.database

import com.homefit.feature.app.AppState
import com.homefit.feature.app.AppStateRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RoomAppStateRepository(database: AppDatabase) : AppStateRepository {
    private val dao = database.appStateSnapshotDao()
    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    override suspend fun load(): AppState? = dao.get()?.let { json.decodeFromString<AppState>(it.json) }

    override suspend fun save(state: AppState) {
        dao.upsert(AppStateSnapshotEntity(json = json.encodeToString(state)))
    }
}
