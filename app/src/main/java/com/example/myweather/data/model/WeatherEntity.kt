package com.example.myweather.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class WeatherEntity(
    @PrimaryKey val query: String,     // use city query as key (one row per city)
    val cityName: String?,
    val tempC: Int?,
    val description: String?,
    val icon: String?,
    val error: String?,
    val updatedAtMillis: Long
)