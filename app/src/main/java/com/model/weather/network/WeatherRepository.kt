package com.model.weather.network

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.model.weather.model.APIResponse
import com.model.weatherapplication.models.WeatherData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class WeatherRepository {
    private val noInternet = "No Internet Connection!"

    private val apiWeatherFrontEnd = ApiClient.getClient()?.create(
        ApiCallable::class.java
    )

    val error = MutableLiveData<Throwable>()

    fun getWeather(lat: Double, lon: Double, appid: String, units: String): MutableLiveData<APIResponse<WeatherData>> {
        val temp = MutableLiveData<APIResponse<WeatherData>>()
        temp.value = APIResponse.loading(null)
        apiWeatherFrontEnd!!.getTemp(lat,lon,appid, units).enqueue(object : Callback<WeatherData> {
            override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
                if (response.isSuccessful) {
                    val weather = response.body()
                    temp.value = APIResponse.success(weather)
                } else {
                    temp.value = response.body()!!.message?.let { APIResponse.error(it.toString(), null) }
                }
            }

            override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                if (t is IOException) {
                    temp.value = APIResponse.error(noInternet, null)
                } else {
                    temp.value = APIResponse.error(t.message.toString(), null)
                }
            }

        })
        return temp
    }
}