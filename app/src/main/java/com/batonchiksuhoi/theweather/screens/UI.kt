package com.batonchiksuhoi.theweather.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material3.AlertDialogDefaults.containerColor

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.batonchiksuhoi.theweather.model.WeatherModel
import com.batonchiksuhoi.theweather.ui.theme.SkyBlue


//@Preview(showBackground = true)
@Composable
fun ListItem(item: WeatherModel, currentDay: MutableState<WeatherModel>){
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 3.dp)
        .clickable {
            if (item.hours.isEmpty()) return@clickable
            currentDay.value = item
        },
        backgroundColor = SkyBlue,
        elevation = 0.dp,
        shape = RoundedCornerShape(5.dp)
        ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 8.dp, top = 5.dp, bottom = 5.dp)
            ) {
                Text(text = item.time,
                    color = Color.White)
                Text(text = item.condition,
                    color = Color.White)
            }
            Text(
                text = item.currentTemp.ifEmpty { item.maxTemp + "°C/" + item.minTemp} + "°C",
                color = Color.White,
                style = TextStyle(fontSize = 25.sp)
            )
            AsyncImage(
                model = "https:${item.icon}",
                contentDescription = "icon weather",
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 8.dp)
            )
        }
    }
}

@Composable
fun DialogSearch(dialogState: MutableState<Boolean>, onSubmit: (String) -> Unit){
    val dialogText = remember {
        mutableStateOf("")
    }
    AlertDialog(
        onDismissRequest = {
            dialogState.value = false
        },
        confirmButton = {
            TextButton(onClick = {
                onSubmit(dialogText.value)
                dialogState.value = false
            }) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                dialogState.value = false
            }) {
                Text(text = "Cancel")
            }
        },
        title = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Write city name")
                TextField(value = dialogText.value, onValueChange = {
                    dialogText.value = it
                })
            }


        },
    )
}