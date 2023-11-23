package com.jossy.homesync.datasource.local.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice

typealias BluetoothDeviceAlias = VisibleBluetoothDevice

data class VisibleBluetoothDevice(
	val name: String?,
	val address: String
)

@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceAlias(): BluetoothDeviceAlias = VisibleBluetoothDevice(
	name = this.name,
	address = this.address
)