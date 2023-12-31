package com.jossy.homesync.tools.bluetooth.receiver

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

@Suppress("DEPRECATION")
class BluetoothStateReceiver(
	private val onStateChanged: (state: String, BluetoothDevice) -> Unit
) : BroadcastReceiver() {
	override fun onReceive(context: Context?, intent: Intent?) {
		getDeviceFromIntent(intent)?.let {
			verifyDevice(it, intent)
		}
	}

	private fun getDeviceFromIntent(intent: Intent?) =
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			intent?.getParcelableExtra(
				BluetoothDevice.EXTRA_DEVICE,
				BluetoothDevice::class.java
			)
		} else {
			intent?.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
		}

	private fun verifyDevice(device: BluetoothDevice, intent: Intent?) {
		intent?.action?.let {
			onStateChanged(it, device)
		}
	}
}