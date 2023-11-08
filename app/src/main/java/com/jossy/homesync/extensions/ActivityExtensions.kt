package com.jossy.homesync.extensions

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity


fun ComponentActivity.getBluetoothManager(): BluetoothManager? =
	this.getSystemService(BluetoothManager::class.java)

fun ComponentActivity.getBluetoothAdapter() = this.getBluetoothManager()?.adapter

fun ComponentActivity.hasPermission(permission: String): Boolean =
	this.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED

@SuppressLint("UnspecifiedRegisterReceiverFlag")
fun ComponentActivity.registerBluetoothReceiver(broadcastReceiver: BroadcastReceiver) =
	this.registerReceiver(
		broadcastReceiver,
		IntentFilter(android.bluetooth.BluetoothDevice.ACTION_FOUND)
	)

@SuppressLint("UnspecifiedRegisterReceiverFlag")
fun ComponentActivity.unregisterBluetoothReceiver(broadcastReceiver: BroadcastReceiver) =
	this.unregisterReceiver(
		broadcastReceiver
	)