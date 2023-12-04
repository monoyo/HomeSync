package com.jossy.device.domain.api

import com.jossy.device.data.remote.SonoffStateBody
import retrofit2.http.Body
import retrofit2.http.POST

interface SonoffApi {
	@POST("zeroconf/switch")
	suspend fun switchState(@Body body: SonoffStateBody)
}