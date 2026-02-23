package com.example.myweather.data.repository

import com.example.myweather.data.local.WeatherDao
import com.example.myweather.data.model.WeatherEntity
import com.example.myweather.data.remote.WeatherApi
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.io.IOException

class WeatherRepository(
    private val api: WeatherApi,
    private val dao: WeatherDao
) {
    private val cacheMillis = 30 * 60 * 1000L
    fun observeAll(): Flow<List<WeatherEntity>> = dao.observeAll()
    suspend fun deleteCity(query: String) = dao.deleteByQuery(query)
    suspend fun cleanupExpired() {
        val now = System.currentTimeMillis()
        dao.deleteOlderThan(now - cacheMillis)
    }

    suspend fun refreshIfNeeded(query: String, apiKey: String) {
        val now = System.currentTimeMillis()

        cleanupExpired()

        val cached = dao.getByQuery(query)
        val isFresh = cached != null &&
                (now - cached.updatedAtMillis) <= cacheMillis

        if (isFresh) return

        try {
            val res = api.getWeatherByCity(query, apiKey)

            dao.upsert(
                WeatherEntity(
                    query = query,
                    cityName = res.name,
                    tempC = res.main.temp.toInt(),
                    description = res.weather.firstOrNull()?.description,
                    icon = res.weather.firstOrNull()?.icon,
                    error = null,
                    updatedAtMillis = now
                )
            )
        } catch (e: HttpException) {
            dao.upsert(
                WeatherEntity(
                    query = query,
                    cityName = null,
                    tempC = null,
                    description = null,
                    icon = null,
                    error = "Virhe (${e.code()})",
                    updatedAtMillis = now
                )
            )
        } catch (_: IOException) {
            dao.upsert(
                WeatherEntity(
                    query = query,
                    cityName = null,
                    tempC = null,
                    description = null,
                    icon = null,
                    error = "Verkkovirhe",
                    updatedAtMillis = now
                )
            )
        }
    }
}