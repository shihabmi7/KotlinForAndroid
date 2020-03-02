package com.shihab.kotlintoday.json_test

import com.squareup.moshi.Json

data class BookedRoomsItem(

	@Json(name="images")
	val images: String? = null,

	@Json(name="price")
	val price: Int? = null,

	@Json(name="adults")
	val adults: Int? = null,

	@Json(name="name")
	val name: String? = null,

	@Json(name="childs")
	val childs: Int? = null,

	@Json(name="points")
	val points: String? = null
)