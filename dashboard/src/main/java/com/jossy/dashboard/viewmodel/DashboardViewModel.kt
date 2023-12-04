package com.jossy.dashboard.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.lifecycle.ViewModel
import com.jossy.dashboard.data.IoTDevice
import com.jossy.dashboard.data.ItemType

class DashboardViewModel: ViewModel() {
	private val _devicesList = mutableListOf<IoTDevice>()
	val devicesList: List<IoTDevice>
		get() = _devicesList

	init {
		_devicesList.add(IoTDevice(0, "Switch", "Sonoff R1", Icons.Filled.FavoriteBorder, ItemType.Switch))
	}
}