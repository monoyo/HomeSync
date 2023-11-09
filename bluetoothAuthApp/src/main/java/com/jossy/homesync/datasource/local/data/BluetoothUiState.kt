package com.jossy.homesync.datasource.local.data

import com.jossy.homesync.tools.bluetooth.data.BluetoothDevice


data class BluetoothUiState(
	val scannedDevices: List<BluetoothDevice> = emptyList(),
	val pairedDevices: List<BluetoothDevice> = emptyList(),
	val isConnected: Boolean = false,
	val isConnecting: Boolean = false,
	val errorMessage: String? = null
)