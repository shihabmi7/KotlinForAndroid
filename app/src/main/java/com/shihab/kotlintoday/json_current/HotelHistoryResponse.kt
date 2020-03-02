package com.shihab.kotlintoday.json_current

import com.squareup.moshi.Json

data class HotelHistoryResponse(

	@Json(name="rooms")
	val rooms: String? = null,

	@Json(name="totalPrice")
	val totalPrice: Int? = null,

	@Json(name="nonRefundable")
	val nonRefundable: Boolean? = null,

	@Json(name="bookedRooms")
	val bookedRooms: List<BookedRoomsItem?>? = null,

	@Json(name="tripCoin")
	val tripCoin: Int? = null,

	@Json(name="checkin")
	val checkin: String? = null,

	@Json(name="voucherId")
	val voucherId: String? = null,

	@Json(name="specialNote")
	val specialNote: String? = null,

	@Json(name="guests")
	val guests: Guests? = null,

	@Json(name="hotel")
	val hotel: Hotel? = null,

	@Json(name="currency")
	val currency: String? = null,

	@Json(name="discountValue")
	val discountValue: Int? = null,

	@Json(name="freeCancellationDate")
	val freeCancellationDate: Any? = null,

	@Json(name="checkout")
	val checkout: String? = null,

	@Json(name="bundle")
	val bundle: Boolean? = null,

	@Json(name="paymentStatus")
	val paymentStatus: String? = null,

	@Json(name="status")
	val status: String? = null,

	@Json(name="totalNights")
	val totalNights: Int? = null
)