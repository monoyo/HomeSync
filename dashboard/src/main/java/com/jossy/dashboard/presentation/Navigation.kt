package com.jossy.dashboard.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jossy.dashboard.presentation.screen.DashboardScreen

@Composable
fun Navigation(){
	val navigation = rememberNavController()
	NavHost(navController = navigation, startDestination = Screen.Dashboard.route) {
		composable(Screen.Dashboard.route) {
			DashboardScreen(navigation)
		}
	}
}

sealed class Screen(val route: String) {
	object Dashboard: Screen(DASHBOARD)

	companion object {
		private const val DASHBOARD = "dashboard"
	}
}