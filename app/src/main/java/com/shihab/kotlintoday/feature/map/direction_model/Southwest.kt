package com.example.example

import com.google.gson.annotations.SerializedName


data class Southwest(

    @SerializedName("lat") var lat: Double? = null,
    @SerializedName("lng") var lng: Double? = null

)