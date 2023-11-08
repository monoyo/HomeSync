package com.jossy.homesync.datasource.local

import com.jossy.homesync.datasource.remote.bluetooth.data.BluetoothDevice


data class BluetoothUiState(
	val scannedDevices: List<BluetoothDevice> = emptyList(),
	val pairedDevices: List<BluetoothDevice> = emptyList()
)