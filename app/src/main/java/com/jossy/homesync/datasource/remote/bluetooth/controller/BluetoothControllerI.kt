package com.jossy.homesync.datasource.remote.bluetooth.controller

import com.jossy.homesync.datasource.remote.bluetooth.data.BluetoothDevice
import kotlinx.coroutines.flow.StateFlow

interface BluetoothControllerI {
	val scannedDevices: StateFlow<List<BluetoothDevice>>
	val pairedDevices: StateFlow<List<BluetoothDevice>>

	fun startDiscovery()
	fun stopDiscovery()

	fun release()

}