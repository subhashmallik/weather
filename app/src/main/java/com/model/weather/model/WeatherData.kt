package com.model.weatherapplication.models


import com.google.gson.annotations.SerializedName
import com.model.weather.model.City

data class WeatherData(
    @SerializedName("cnt")
    val cnt: Int?,
    @SerializedName("cod")
    val cod: String?,
    @SerializedName("list")
    val list: List<DataList>?,
    @SerializedName("message")
    val message: Double?,
    @SerializedName("city")
    val city: City?
)