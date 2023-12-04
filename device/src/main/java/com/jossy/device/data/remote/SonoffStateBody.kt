package com.jossy.device.data.remote

import com.google.gson.annotations.SerializedName

data class SonoffStateBody(
	val data: String,
	@SerializedName("deviceid") val deviceId: String,
	val encrypt: Boolean,
	val iv: String,
	val selfApikey: String,
	val sequence: String
)
