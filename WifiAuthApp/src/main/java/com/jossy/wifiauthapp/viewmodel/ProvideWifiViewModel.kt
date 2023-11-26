package com.jossy.wifiauthapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.jossy.wifiauthapp.data.WifiData
import com.jossy.wifiauthapp.domain.CredentialsEncryptedPreferences
import kotlinx.coroutines.flow.MutableStateFlow

class ProvideWifiViewModel: ViewModel() {
	val state = MutableStateFlow(
		WifiData()
	)

	fun nameChanged(value: String) {
		state.tryEmit(
			state.value.copy(
				name = value
			)
		)
	}
	fun passwordChanged(value: String) {
		state.tryEmit(
			state.value.copy(
				password = value
			)
		)
	}

	fun getSavedState(context: Context) {
		state.tryEmit(
			CredentialsEncryptedPreferences.getCredentials(context)
		)
	}

	fun saveState(context: Context) {
		CredentialsEncryptedPreferences.saveCredentials(context, state.value)
	}
}