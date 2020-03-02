package com.shihab.kotlintoday.json_test

import com.squareup.moshi.Json

data class Guests(

    @Json(name="rooms")
	val rooms: List<List<RoomsItemItem?>?>? = null,

    @Json(name="primaryContact")
	val primaryContact: PrimaryContact? = null
)