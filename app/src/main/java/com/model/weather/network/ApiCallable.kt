package com.model.weather.network

import com.model.weatherapplication.models.WeatherData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiCallable {
    @GET("forecast/")
    fun getTemp(@Query("lat") lat: Double, @Query("lon") lon: Double, @Query("appid") appid: String, @Query("units") units: String): Call<WeatherData>

}