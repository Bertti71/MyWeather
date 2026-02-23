package com.example.myweather.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myweather.data.model.WeatherEntity

@Database(entities = [WeatherEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}