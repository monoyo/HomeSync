package com.jossy.wifiauthapp.domain.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitObject {
	private var instance: Retrofit? = null
	private val interceptor = HttpLoggingInterceptor().apply {
		setLevel(HttpLoggingInterceptor.Level.BODY)
	}

	fun getInstance(url: String): Retrofit =
		instance ?: Retrofit.Builder()
			.baseUrl(url)
			.client(
				OkHttpClient.Builder().addInterceptor(interceptor).build()
			)
			.addConverterFactory(GsonConverterFactory.create())
			.build()
			.apply {
			instance = this
		}
}