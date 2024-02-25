package com.model.weatherapplication.models


import com.google.gson.annotations.SerializedName
import com.model.weather.model.City

data class DataList (
    @SerializedName("dt")
    val dt: Long?,
    @SerializedName("dt_txt")
    val dtTxt: String?,
    @SerializedName("main")
    val main: Main?,

)