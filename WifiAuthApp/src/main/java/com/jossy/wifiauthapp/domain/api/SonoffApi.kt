package com.jossy.wifiauthapp.domain.api

import com.jossy.wifiauthapp.data.SonoffStateBody
import retrofit2.http.Body
import retrofit2.http.POST

interface SonoffApi {
	@POST("zeroconf/switch")
	suspend fun switchState(@Body body: SonoffStateBody)
}