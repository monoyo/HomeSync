package com.jossy.wifiauthapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jossy.wifiauthapp.presentation.screen.WifiCredentialsScreen
import com.jossy.wifiauthapp.presentation.screen.WifiDevicesScreen

@Composable
fun WifiAuthNavigation() {
	val navController = rememberNavController()
	NavHost(navController = navController, startDestination = WifiScreen.CredentialsScreen.route) {
		composable(WifiScreen.CredentialsScreen.route) { WifiCredentialsScreen(navController) }
		composable(WifiScreen.WifiDevicesScreen.route) { WifiDevicesScreen(navController) }
	}
}