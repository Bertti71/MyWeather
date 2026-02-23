package com.example.myweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.myweather.data.local.AppDatabase
import com.example.myweather.data.remote.RetrofitInstance
import com.example.myweather.data.repository.WeatherRepository
import com.example.myweather.ui.theme.MyWeatherTheme
import com.example.myweather.ui.theme.WeatherScreen
import com.example.myweather.viewmodel.WeatherViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "weather_database"
        )
            .fallbackToDestructiveMigration()
            .build()
        val repository = WeatherRepository(
            api = RetrofitInstance.api,
            dao = database.weatherDao()
        )
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return WeatherViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
        setContent {
            MyWeatherTheme {
                val vm: WeatherViewModel = viewModel(factory = factory)
                WeatherScreen(viewModel = vm)
            }
        }
    }
}