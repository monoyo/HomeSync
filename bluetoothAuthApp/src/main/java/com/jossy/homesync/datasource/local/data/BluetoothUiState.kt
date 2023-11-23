package com.jossy.homesync.datasource.local.data


data class BluetoothUiState(
	val scannedDevices: List<VisibleBluetoothDevice> = emptyList(),
	val pairedDevices: List<VisibleBluetoothDevice> = emptyList(),
	val connectionResult: ConnectionResult = ConnectionResult.NoConnection)