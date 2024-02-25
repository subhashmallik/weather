package com.model.weather.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.model.weather.model.APIResponse
import com.model.weather.network.WeatherRepository
import com.model.weatherapplication.models.WeatherData

class MainViewModel : ViewModel() {
    private val weatherRepository = WeatherRepository()
    fun getTemp(lat: Double, lon: Double, appid: String, units: String): LiveData<APIResponse<WeatherData>> {
        return weatherRepository.getWeather(lat, lon, appid, units)
    }

}