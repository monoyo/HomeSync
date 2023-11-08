package com.jossy.homesync.presentation

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jossy.homesync.datasource.local.BluetoothUiState
import com.jossy.homesync.datasource.remote.bluetooth.controller.BluetoothController
import com.jossy.homesync.datasource.remote.bluetooth.controller.BluetoothControllerI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class BluetoothViewModel(
	bluetoothAdapter: BluetoothAdapter?,
	hasPermission: (String) -> Boolean,
	registerReceiver: (BroadcastReceiver) -> Intent?,
	unregisterReceiver: (BroadcastReceiver) -> Unit,
	private val bluetoothController: BluetoothControllerI = BluetoothController(
		bluetoothAdapter,
		hasPermission,
		registerReceiver,
		unregisterReceiver
	)
) : ViewModel() {

	private val _state = MutableStateFlow(BluetoothUiState())
	val state = combine(
		bluetoothController.scannedDevices,
		bluetoothController.pairedDevices,
		_state
	) { scannedDevices, pairedDevices, state ->
		state.copy(
			scannedDevices = scannedDevices,
			pairedDevices = pairedDevices
		)
	}.stateIn(
		viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value
	)

	fun startScan() {
		bluetoothController.startDiscovery()
	}

	fun stopScan() {
		bluetoothController.stopDiscovery()
	}
}
