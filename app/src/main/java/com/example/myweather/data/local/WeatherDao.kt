package com.example.myweather.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myweather.data.model.WeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Query("SELECT * FROM weather_cache ORDER BY updatedAtMillis DESC")
    fun observeAll(): Flow<List<WeatherEntity>>
    @Query("SELECT * FROM weather_cache WHERE query = :query LIMIT 1")
    suspend fun getByQuery(query: String): WeatherEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: WeatherEntity)
    @Query("DELETE FROM weather_cache WHERE query = :query")
    suspend fun deleteByQuery(query: String)
    @Query("DELETE FROM weather_cache WHERE updatedAtMillis < :minTimeMillis")
    suspend fun deleteOlderThan(minTimeMillis: Long)
}