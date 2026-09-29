package com.homefit.feature.app

interface AppStateRepository {
    suspend fun load(): AppState?
    suspend fun save(state: AppState)
}
