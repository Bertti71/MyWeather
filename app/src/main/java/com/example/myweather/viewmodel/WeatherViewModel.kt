package com.example.myweather.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myweather.BuildConfig
import com.example.myweather.data.model.WeatherEntity
import com.example.myweather.data.repository.WeatherRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CityWeatherItem(
    val query: String,
    val cityName: String?,
    val tempC: Int?,
    val description: String?,
    val icon: String?,
    val error: String?
)

data class WeatherUiState(
    val query: String = "",
    val items: List<CityWeatherItem> = emptyList(),
    val globalError: String? = null
)

class WeatherViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    private val queryFlow = MutableStateFlow("")
    private val globalErrorFlow = MutableStateFlow<String?>(null)

    val uiState: StateFlow<WeatherUiState> =
        combine(
            queryFlow,
            repository.observeAll().map { list -> list.map { it.toUiItem() } },
            globalErrorFlow
        ) { query, items, error ->
            WeatherUiState(query, items, error)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WeatherUiState())

    init {
        viewModelScope.launch {
            repository.cleanupExpired()
        }
    }

    fun onQueryChange(value: String) {
        queryFlow.value = value
        globalErrorFlow.value = null
    }

    fun removeCity(query: String) {
        viewModelScope.launch {
            repository.deleteCity(query)
        }
    }

    fun fetchAndAddCity() {
        val q = queryFlow.value.trim()
        if (q.isEmpty()) {
            globalErrorFlow.value = "Syötä kaupunki"
            return
        }
        if (BuildConfig.OPENWEATHER_API_KEY.isBlank()) {
            globalErrorFlow.value = "API-avain puuttuu"
            return
        }
        viewModelScope.launch {
            repository.refreshIfNeeded(q, BuildConfig.OPENWEATHER_API_KEY)
        }
    }
}

private fun WeatherEntity.toUiItem() =
    CityWeatherItem(
        query = query,
        cityName = cityName,
        tempC = tempC,
        description = description,
        icon = icon,
        error = error
    )