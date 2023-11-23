package com.jossy.homesync.datasource.local.data

data class ReceivedData(
	val bluetoothDevice: BluetoothDeviceAlias,
	val list: List<String>
)
