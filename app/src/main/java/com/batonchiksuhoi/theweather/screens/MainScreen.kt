package com.batonchiksuhoi.theweather.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.batonchiksuhoi.theweather.R
import com.batonchiksuhoi.theweather.model.WeatherModel
import com.batonchiksuhoi.theweather.ui.theme.SkyBlue
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun MainList(list: List<WeatherModel>, currentDay: MutableState<WeatherModel>){
    LazyColumn(modifier = Modifier.fillMaxSize()){
        itemsIndexed(
            list
        ){
                _,item ->
            ListItem(item, currentDay)
        }
    }
}

//Preview(showBackground = true)
@Composable
fun MainCard(currentWeather: MutableState<WeatherModel>, onClickSync: () -> Unit, onClickSearch: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(5.dp),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(SkyBlue),
            elevation = CardDefaults.cardElevation(5.dp),
            shape = RoundedCornerShape(5.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = currentWeather.value.time,
                        style = TextStyle(fontSize = 15.sp),
                        color = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )
                    AsyncImage(
                        model = "https:${currentWeather.value.icon}",
                        contentDescription = "icon weather",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 8.dp)
                    )
                }
                Text(
                    text = currentWeather.value.city,
                    style = TextStyle(fontSize = 35.sp),
                    color = Color.White,
                )
                Text(
                    text = if(currentWeather.value.currentTemp.isNotEmpty())
                        currentWeather.value.currentTemp + "°C"
                    else "",
                    style = TextStyle(fontSize = 65.sp),
                    color = Color.White,
                )
                Text(
                    text = currentWeather.value.condition,
                    style = TextStyle(fontSize = 30.sp),
                    color = Color.White,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    IconButton(onClick = {
                        onClickSearch.invoke()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = "icon search",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "${currentWeather.value.maxTemp}°C / ${currentWeather.value.minTemp}°C",
                        style = TextStyle(fontSize = 30.sp),
                        color = Color.White,
                    )
                    IconButton(onClick = {
                        onClickSync.invoke()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_sync),
                            contentDescription = "icon sync",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabLayout(daysList: MutableState<List<WeatherModel>>, currentWeather: MutableState<WeatherModel>) {
    val tabList = listOf("HOURS", "DAYS")
    val pagerState = rememberPagerState()
    val tabIndex = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .padding(5.dp)
            .clip(RoundedCornerShape(5.dp))
    ) {
        TabRow(
            selectedTabIndex = tabIndex,
            indicator = {position ->
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(position[tabIndex])
                        )
            },
            containerColor = SkyBlue,
            contentColor = Color.White
            ) {
            tabList.forEachIndexed {index, text ->
                Tab(
                    selected = false,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(
                            text = text,
                            color = Color.White,
                        )
                    })
            }
        }
        HorizontalPager(
            count = tabList.size,
            state = pagerState,
            modifier = Modifier.weight(1.0f)
        ) { index ->
            val list = when(index){
                0 -> getWeatherByHours(currentWeather.value.hours)
                1 -> daysList.value
                else -> daysList.value
            }
            MainList(list, currentWeather)
        }
    }
}

private fun getWeatherByHours(hours: String): List<WeatherModel>{
    if (hours.isEmpty()) return listOf()
    val hoursArray = JSONArray(hours)
    val list = ArrayList<WeatherModel>()
    for (i in 0 until hoursArray.length()){
        val item = hoursArray[i] as JSONObject
        list.add(
            WeatherModel(
                "",
                item.getString("time"),
                item.getString("temp_c"),
                item.getJSONObject("condition").getString("text"),
                item.getJSONObject("condition").getString("icon"),
                "",
                "",
                "",
            )
        )
    }
    return list
}