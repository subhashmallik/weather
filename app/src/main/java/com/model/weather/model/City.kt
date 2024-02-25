package com.model.weather.model


import com.google.gson.annotations.SerializedName

data class City(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?
)