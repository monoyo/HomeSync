package com.jossy.wifiauthapp.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.jossy.wifiauthapp.presentation.navigation.WifiScreen
import com.jossy.wifiauthapp.viewmodel.ProvideWifiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiCredentialsScreen(navController: NavHostController) {
	val viewModel: ProvideWifiViewModel = viewModel()
	val context = LocalContext.current
	val state = viewModel.state.collectAsState()
	viewModel.getSavedState(context)
	Surface(modifier = Modifier.fillMaxSize()) {
		Column(
			modifier = Modifier.fillMaxSize(),
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			TextField(value = state.value.name ?: "", onValueChange = {
				viewModel.nameChanged(it)
			})
			TextField(value = state.value.password ?: "", onValueChange = {
				viewModel.passwordChanged(it)
			})
			Button(onClick = {
				viewModel.saveState(context)
				navController.navigate(WifiScreen.WifiDevicesScreen.route)
			}) {
				Text(text = "Save credentials")
			}
		}
	}
}