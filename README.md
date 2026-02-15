## Week5 – Weather application 

### Toteutetut ominaisuudet
- Retrofit on kirjasto, jolla tehdään HTTP-pyyntöjä (esim. haetaan sää OpenWeather API:sta). Se hoitaa yhteyden palvelimeen ja palauttaa vastauksen sovellukselle.
- OpenWeather palauttaa datan JSON-muodossa.
- Gson muuntaa JSON-vastauksen automaattisesti Kotlinin dataluokiksi (WeatherResponse).
- API-kutsu tehdään taustasäikeessä coroutineilla (viewModelScope.launch). Kun data saapuu, UI päivittyy automaattisesti.
- ViewModel hallitsee WeatherUiState-oliota (loading, error, data).
- Compose reagoi tilamuutoksiin ja piirtää näkymän uudelleen.
- API-key on local.properties-tiedostossa -> Gradle siirtää sen BuildConfigiin -> Retrofit käyttää avainta API-kutsussa.
