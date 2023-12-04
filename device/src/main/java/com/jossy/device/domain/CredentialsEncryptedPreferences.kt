package com.jossy.device.domain

/*
import com.jossy.wifiauthapp.data.WifiData


object CredentialsEncryptedPreferences {
	private const val PREFERENCES_NAME: String = "NAME_AND_PASSWORD_1295_23651_6784_234757447FTS"
	private const val NAME: String = "NAME1295_23651_6784_234757447FTS"
	private const val PASSWORD: String = "NAME1295_23651_6784_234757447FTS"

	fun getCredentials(context: Context): WifiData {
		val preferences = getSharedPreferences(context)
		val name = getString(preferences, NAME)
		val password = getString(preferences, PASSWORD)
		return WifiData(name, password)
	}

	fun saveCredentials(context: Context, wifiData: WifiData) {
		getSharedPreferences(context)
			.edit()
			.putString(NAME, wifiData.name)
			.putString(PASSWORD, wifiData.password)
			.apply()
	}

	private fun getString(
		preferences: SharedPreferences,
		name: String
	) = preferences.getString(name,"")

	private fun getSharedPreferences(context: Context) = EncryptedSharedPreferences.create(
		PREFERENCES_NAME,
		getMasterKeys(),
		context,
		EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
		EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
	)

	private fun getMasterKeys() = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
}*/
