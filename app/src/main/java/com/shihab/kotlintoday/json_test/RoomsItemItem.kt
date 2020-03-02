package com.shihab.kotlintoday.json_test

import com.squareup.moshi.Json

data class RoomsItemItem(

    @Json(name = "titleName")
    val titleName: String? = null,

    @Json(name = "surName")
    val surName: String? = null,

    @Json(name = "nationality")
    val nationality: String? = null,

    @Json(name = "address1")
    val address1: String? = null,

    @Json(name = "mobileNumber")
    val mobileNumber: String? = null,

    @Json(name = "givenName")
    val givenName: String? = null,

    @Json(name = "travellerType")
    val travellerType: String? = null,

    @Json(name = "age")
    val age: Any? = null,

    @Json(name = "email")
    val email: String? = null
)