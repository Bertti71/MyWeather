package com.example.myweather.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myweather.data.model.WeatherResponse
import kotlin.math.roundToInt

@Composable
fun WeatherResultSection(result: WeatherResponse) {
    val temp = result.main.temp.roundToInt()
    val desc = result.weather.firstOrNull()?.description ?: "-"

    Text(text = result.name, style = MaterialTheme.typography.headlineSmall)
    Spacer(modifier = Modifier.height(8.dp))
    Text(text = "$temp °C", style = MaterialTheme.typography.headlineMedium)
    Spacer(modifier = Modifier.height(8.dp))
    Text(text = desc)
}
