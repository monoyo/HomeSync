package com.jossy.wifiauthapp.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jossy.wifiauthapp.controller.WifiControllerActions
import com.jossy.wifiauthapp.extensions.getActivity
import com.jossy.wifiauthapp.presentation.FindDeviceActivity
import com.jossy.wifiauthapp.viewmodel.WifiDevicesViewModel

@Composable
fun WifiDevicesScreen(navController: NavController) {
	val viewModel: WifiDevicesViewModel = viewModel()
	val context = LocalContext.current
	val wifiActions = context.getActivity<FindDeviceActivity>() as WifiControllerActions
	Surface(modifier = Modifier.fillMaxSize()) {
		Column(
			modifier = Modifier.fillMaxSize(),
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			/*LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp),
				content = lazyColumnState)*/
			Text(text = "JAKIS TEKST")
			Button(onClick = {
				wifiActions.addAction()
				wifiActions.scanDevices()
			}) {
				Text(text = "SCAN")
			}
		}
	}
}