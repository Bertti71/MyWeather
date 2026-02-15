package com.example.myweather.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myweather.viewmodel.CityWeatherItem
import com.example.myweather.viewmodel.WeatherViewModel

@Composable
fun WeatherScreen(viewModel: WeatherViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Sää",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.query,
            onValueChange = viewModel::onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("Hae kaupunki") }
        )

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = { viewModel.fetchAndAddCity() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Hae sää")
        }

        uiState.globalError?.let {
            Spacer(Modifier.height(10.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(uiState.items, key = { it.id }) { item ->
                WeatherCard(
                    item = item,
                    onRemove = { viewModel.removeItem(item.id) }
                )
            }
        }
    }
}

@Composable
private fun WeatherCard(
    item: CityWeatherItem,
    onRemove: () -> Unit
) {
    val shape = RoundedCornerShape(22.dp)

    val bg = Brush.linearGradient(
        listOf(
            Color(0xFF6C7BFF).copy(alpha = 0.35f),
            Color(0xFF9B8CFF).copy(alpha = 0.20f),
            Color(0xFF4A4A4A).copy(alpha = 0.15f)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(bg)
            .padding(14.dp)
    ) {
        IconButton(
            onClick = onRemove,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Poista")
        }

        Column(modifier = Modifier.padding(end = 32.dp)) {
            Text(
                text = item.cityName ?: item.query,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(6.dp))

            when {
                item.isLoading -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(10.dp))
                        Text("Haetaan...")
                    }
                }

                item.error != null -> {
                    Text(item.error, color = MaterialTheme.colorScheme.error)
                }

                else -> {
                    val tempText = item.tempC?.let { "$it°" } ?: "-"
                    Text(
                        text = tempText,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(text = item.description ?: "-")
                }
            }
        }
    }
}
