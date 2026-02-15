package com.example.myweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.myweather.ui.WeatherScreen
import com.example.myweather.viewmodel.WeatherViewModel
import com.example.myweather.ui.theme.MyWeatherTheme

class MainActivity : ComponentActivity() {

    private val viewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyWeatherTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }
    }
}
