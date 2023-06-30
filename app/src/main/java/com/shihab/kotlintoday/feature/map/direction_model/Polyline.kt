package com.example.example

import com.google.gson.annotations.SerializedName


data class Polyline(

    @SerializedName("points") var points: String? = null

)