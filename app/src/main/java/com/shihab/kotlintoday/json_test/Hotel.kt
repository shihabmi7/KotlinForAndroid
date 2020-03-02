package com.shihab.kotlintoday.json_test

import com.squareup.moshi.Json

data class Hotel(

	@Json(name="amenities")
	val amenities: String? = null,

	@Json(name="images")
	val images: String? = null,

	@Json(name="thumbnail")
	val thumbnail: String? = null,

	@Json(name="address")
	val address: String? = null,

	@Json(name="kind")
	val kind: String? = null,

	@Json(name="center")
	val center: String? = null,

	@Json(name="postalCode")
	val postalCode: String? = null,

	@Json(name="rating")
	val rating: Int? = null,

	@Json(name="policyStruct")
	val policyStruct: String? = null,

	@Json(name="amenityGroups")
	val amenityGroups: String? = null,

	@Json(name="createdAt")
	val createdAt: String? = null,

	@Json(name="cityName")
	val cityName: String? = null,

	@Json(name="descriptionStruct")
	val descriptionStruct: String? = null,

	@Json(name="phone")
	val phone: Any? = null,

	@Json(name="name")
	val name: String? = null,

	@Json(name="indexId")
	val indexId: String? = null,

	@Json(name="countryName")
	val countryName: String? = null,

	@Json(name="starRating")
	val starRating: Int? = null,

	@Json(name="email")
	val email: Any? = null,

	@Json(name="updatedAt")
	val updatedAt: String? = null
)