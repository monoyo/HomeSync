package com.jossy.homesync.tools.bluetooth.controller

import com.jossy.homesync.tools.bluetooth.connection.ConnectionResult
import com.jossy.homesync.tools.bluetooth.data.BluetoothDevice
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothControllerI {
	val isConnected: StateFlow<Boolean>
	val scannedDevices: StateFlow<List<BluetoothDevice>>
	val pairedDevices: StateFlow<List<BluetoothDevice>>
	val errors: SharedFlow<String>

	fun startDiscovery()

	fun stopDiscovery()

	fun startBluetoothServer(): Flow<ConnectionResult>

	fun stopBluetoothServer()

	fun connectToDevice(device: BluetoothDevice, dispatcher: CoroutineDispatcher): Flow<ConnectionResult>

	fun release()

	fun updatePairedDevices()

}