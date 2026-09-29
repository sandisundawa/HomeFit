package com.homefit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.room3.Room
import com.homefit.app.HomeFitApp
import com.homefit.core.database.AppDatabase
import com.homefit.core.database.RoomAppStateRepository
import com.homefit.core.database.withHomeFitDefaults
import com.homefit.feature.app.HomeFitViewModel

class MainActivity : ComponentActivity() {
    private val database by lazy {
        Room.databaseBuilder<AppDatabase>(applicationContext, "homefit.db")
            .withHomeFitDefaults()
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
    private val repository by lazy { RoomAppStateRepository(database) }
    private val appViewModel by lazy {
        val factory = viewModelFactory { initializer { HomeFitViewModel(repository) } }
        ViewModelProvider(this, factory)[HomeFitViewModel::class]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { HomeFitApp(appViewModel) }
    }
}
