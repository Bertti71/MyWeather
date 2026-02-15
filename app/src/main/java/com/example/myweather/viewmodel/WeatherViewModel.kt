package com.example.myweather.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myweather.BuildConfig
import com.example.myweather.data.model.WeatherResponse
import com.example.myweather.data.remote.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.util.UUID

data class CityWeatherItem(
    val id: String = UUID.randomUUID().toString(),
    val query: String,
    val cityName: String? = null,
    val tempC: Int? = null,
    val description: String? = null,
    val icon: String? = null,
    val error: String? = null,
    val isLoading: Boolean = false
)

data class WeatherUiState(
    val query: String = "",
    val items: List<CityWeatherItem> = emptyList(),
    val globalError: String? = null
)

class WeatherViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState

    fun onQueryChange(newValue: String) {
        _uiState.update { it.copy(query = newValue, globalError = null) }
    }

    fun removeItem(id: String) {
        _uiState.update { state ->
            state.copy(items = state.items.filterNot { it.id == id })
        }
    }

    fun fetchAndAddCity() {
        val q = uiState.value.query.trim()

        if (q.isEmpty()) {
            _uiState.update { it.copy(globalError = "Syötä kaupunki") }
            return
        }

        if (BuildConfig.OPENWEATHER_API_KEY.isBlank()) {
            _uiState.update { it.copy(globalError = "API-avain puuttuu") }
            return
        }

        val loadingItem = CityWeatherItem(query = q, isLoading = true)
        _uiState.update { it.copy(items = listOf(loadingItem) + it.items, query = "") }

        viewModelScope.launch {
            try {
                val res: WeatherResponse = RetrofitInstance.api.getWeatherByCity(
                    city = q,
                    apiKey = BuildConfig.OPENWEATHER_API_KEY
                )

                val filled = loadingItem.copy(
                    cityName = res.name,
                    tempC = res.main.temp.toInt(),
                    description = res.weather.firstOrNull()?.description,
                    icon = res.weather.firstOrNull()?.icon,
                    isLoading = false,
                    error = null
                )

                _uiState.update { state ->
                    state.copy(items = state.items.map { if (it.id == loadingItem.id) filled else it })
                }
            } catch (e: HttpException) {
                val msg = when (e.code()) {
                    401 -> "Väärä API-avain (401)"
                    404 -> "Kaupunkia ei löydy"
                    else -> "Virhe haussa (${e.code()})"
                }

                _uiState.update { state ->
                    state.copy(items = state.items.map {
                        if (it.id == loadingItem.id) it.copy(isLoading = false, error = msg) else it
                    })
                }
            } catch (_: IOException) {
                _uiState.update { state ->
                    state.copy(items = state.items.map {
                        if (it.id == loadingItem.id) it.copy(isLoading = false, error = "Verkkovirhe") else it
                    })
                }
            } catch (_: Exception) {
                _uiState.update { state ->
                    state.copy(items = state.items.map {
                        if (it.id == loadingItem.id) it.copy(isLoading = false, error = "Tuntematon virhe") else it
                    })
                }
            }
        }
    }
}
