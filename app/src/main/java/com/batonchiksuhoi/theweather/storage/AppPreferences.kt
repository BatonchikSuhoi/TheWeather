package com.batonchiksuhoi.theweather.storage

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {

    var data: SharedPreferences = context.getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE)

    fun saveCity(city: String) {
        data.edit().putString("CITY", city).apply()
    }
    fun getCity(): String? {
        return data.getString("CITY", "Minsk")
    }
}