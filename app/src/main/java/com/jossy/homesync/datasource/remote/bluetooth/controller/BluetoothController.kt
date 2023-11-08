package com.jossy.homesync.datasource.remote.bluetooth.controller

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Intent
import com.jossy.homesync.datasource.remote.bluetooth.data.BluetoothDevice
import com.jossy.homesync.datasource.remote.bluetooth.data.BluetoothDeviceAlias
import com.jossy.homesync.datasource.remote.bluetooth.data.toBluetoothDeviceAlias
import com.jossy.homesync.datasource.remote.bluetooth.receiver.FoundDeviceReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

@SuppressLint("MissingPermission")
class BluetoothController(
	private val bluetoothAdapter: BluetoothAdapter?,
	private val hasPermission: (String) -> Boolean,
	private val registerReceiver: (BroadcastReceiver) -> Intent?,
	private val unregisterReceiver: (BroadcastReceiver) -> Unit
) : BluetoothControllerI {
	private val _scannedDevices = MutableStateFlow<List<BluetoothDeviceAlias>>(emptyList())

	override val scannedDevices: StateFlow<List<BluetoothDevice>>
		get() = _scannedDevices

	private val _pairedDevices = MutableStateFlow<List<BluetoothDeviceAlias>>(emptyList())

	override val pairedDevices: StateFlow<List<BluetoothDevice>>
		get() = _pairedDevices

	private val foundDeviceReceiver = FoundDeviceReceiver { bluetoothDevice ->
		_scannedDevices.update { bluetoothDevices ->
			val newDevice = bluetoothDevice.toBluetoothDeviceAlias()
			if(newDevice in bluetoothDevices) bluetoothDevices else bluetoothDevices + newDevice
		}

	}

	init {
		updatePairedDevices()
	}

	override fun startDiscovery() {
		if(hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
			updatePairedDevices()
			bluetoothAdapter?.startDiscovery()
			registerReceiver(foundDeviceReceiver)
		}
	}

	override fun stopDiscovery() {
		if(hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
			bluetoothAdapter?.cancelDiscovery()
		}
	}

	override fun release() {
		unregisterReceiver(foundDeviceReceiver)
	}

	private fun updatePairedDevices() {
		if (hasPermission(Manifest.permission.BLUETOOTH_CONNECT))
			bluetoothAdapter?.bondedDevices?.map {
				it.toBluetoothDeviceAlias()
			}?.let { devices ->
				_pairedDevices.update { devices }
			}
	}
}
