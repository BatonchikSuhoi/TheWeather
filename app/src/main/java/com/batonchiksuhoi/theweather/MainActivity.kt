package com.batonchiksuhoi.theweather

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.batonchiksuhoi.theweather.model.Weather
import com.batonchiksuhoi.theweather.model.WeatherModel
import com.batonchiksuhoi.theweather.screens.DialogSearch
import com.batonchiksuhoi.theweather.screens.MainCard
import com.batonchiksuhoi.theweather.screens.TabLayout
import com.batonchiksuhoi.theweather.storage.AppPreferences
import com.batonchiksuhoi.theweather.ui.theme.TheWeatherTheme
import org.json.JSONObject

const val API_KEY = "83b30470150648e9a47160457241501"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TheWeatherTheme {
                val preferences = AppPreferences(this)
                val daysList = remember{
                    mutableStateOf(listOf<WeatherModel>())
                }
                val dialogState = remember {
                    mutableStateOf(false)
                }
                val currentWeather = remember {
                    mutableStateOf(WeatherModel(
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                    ))
                }
                if (dialogState.value){
                    DialogSearch(dialogState, onSubmit = {
                        preferences.saveCity(it)
                        getData(it,this, daysList, currentWeather)
                    })
                }
                getData(preferences.getCity().toString(),this, daysList, currentWeather)
                Image(
                    painter = painterResource(id = R.drawable.background_image),
                    contentDescription = "sky",
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.8f),
                    contentScale = ContentScale.Crop
                )
                Column {
                    MainCard(currentWeather, onClickSync = {
                        getData(preferences.getCity().toString(),this@MainActivity, daysList, currentWeather)
                    }, onClickSearch = {
                        dialogState.value = true
                    })
                    TabLayout(daysList, currentWeather)
                }
            }
        }
    }
}

private fun getData(city: String, context: Context, daysList: MutableState<List<WeatherModel>>, currentWeather: MutableState<WeatherModel>){
    val url = "https://api.weatherapi.com/v1/forecast.json?key=$API_KEY" +
            "&q=$city" +
            "&days=5&aqi=no&alerts=no"

    val queue = Volley.newRequestQueue(context)
    val strRequest = StringRequest(
        Request.Method.GET,
        url,
        { response ->
            val list = getWeatherByDays(response)
            daysList.value = list
            currentWeather.value = list[0]
        },
        {
            Log.d("MyLog", "VolleyError: $it")
        }
    )
    queue.add(strRequest)
}

private fun getWeatherByDays(response: String) : List<WeatherModel>{
    if(response.isEmpty()) return listOf()
    val list = ArrayList<WeatherModel>()
    val mainObject = JSONObject(response)
    val city = mainObject.getJSONObject("location").getString("name")
    val days = mainObject.getJSONObject("forecast").getJSONArray("forecastday")
    for (i in 0 until days.length()){
        val item = days[i] as JSONObject
        list.add(
            WeatherModel(
                city,
                item.getString("date"),
                "",
                item.getJSONObject("day").getJSONObject("condition").getString("text"),
                item.getJSONObject("day").getJSONObject("condition").getString("icon"),
                item.getJSONObject("day").getString("maxtemp_c"),
                item.getJSONObject("day").getString("mintemp_c"),
                item.getJSONArray("hour").toString()
                )
        )

    }
    list[0] = list[0].copy(
        time = mainObject.getJSONObject("current").getString("last_updated") ,
        currentTemp = mainObject.getJSONObject("current").getString("temp_c"),
    )
    return list
}