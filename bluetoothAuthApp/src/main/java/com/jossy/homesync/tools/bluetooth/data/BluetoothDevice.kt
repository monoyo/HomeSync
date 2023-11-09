package com.jossy.homesync.tools.bluetooth.data

import android.annotation.SuppressLint

typealias BluetoothDeviceAlias = BluetoothDevice

data class BluetoothDevice(
	val name: String?,
	val address: String
)

@SuppressLint("MissingPermission")
fun android.bluetooth.BluetoothDevice.toBluetoothDeviceAlias(): BluetoothDeviceAlias = BluetoothDevice(
	name = this.name,
	address = this.address
)