package com.jossy.homesync.datasource.local.repository.bluetooth

import com.jossy.homesync.datasource.local.data.ConnectionResult
import com.jossy.homesync.datasource.local.data.VisibleBluetoothDevice
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothControllerI {
	val scannedDevices: StateFlow<List<VisibleBluetoothDevice>>
	val pairedDevices: StateFlow<List<VisibleBluetoothDevice>>
	val errors: SharedFlow<String>
	val connectionResult: SharedFlow<ConnectionResult>

	fun startDiscovery()

	fun stopDiscovery()

	fun connectToDevice(device: VisibleBluetoothDevice, dispatcher: CoroutineDispatcher): Flow<ConnectionResult>

	fun release()

	fun updatePairedDevices()

}