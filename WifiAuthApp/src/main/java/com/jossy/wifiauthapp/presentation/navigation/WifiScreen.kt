package com.jossy.wifiauthapp.presentation.navigation

sealed class WifiScreen(val route: String)  {
	object CredentialsScreen: WifiScreen(CREDENTIALS_SCREEN)
	object WifiDevicesScreen: WifiScreen(WIFI_DEVICES_SCREEN)

	companion object {
		private const val CREDENTIALS_SCREEN = "credentials_screen"
		private const val WIFI_DEVICES_SCREEN = "wifi_devices_screen"
	}
}
