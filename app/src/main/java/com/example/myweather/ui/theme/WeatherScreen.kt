package com.example.myweather.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myweather.viewmodel.CityWeatherItem
import com.example.myweather.viewmodel.WeatherViewModel

@Composable
fun WeatherScreen(viewModel: WeatherViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize().padding(16.dp)) {

        Text("Sää", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.query,
            onValueChange = viewModel::onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Hae kaupunki") }
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = viewModel::fetchAndAddCity,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Hae sää") }

        uiState.globalError?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(uiState.items, key = { it.query }) { item ->
                WeatherCard(
                    item = item,
                    onRemove = { viewModel.removeCity(item.query) }
                )
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}
@Composable
private fun WeatherCard(
    item: CityWeatherItem,
    onRemove: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text(item.cityName ?: item.query, style = MaterialTheme.typography.titleMedium)
                item.tempC?.let { Text("$it°", style = MaterialTheme.typography.headlineMedium) }
                item.description?.let { Text(it) }
                item.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
            IconButton(onClick = onRemove) {
                Text("X")
            }
        }
    }
}