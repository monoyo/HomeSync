package com.jossy.wifiauthapp.controller

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WifiController(
	private val wifiManager: WifiManager,
	private val registerReceiver: (BroadcastReceiver, IntentFilter) -> Intent?,
	private val isCheckedPermission: (String) -> Boolean,
	private val unregisterReceiver: (BroadcastReceiver) -> Any
): WifiControllerI {

	private val wifiScanReceiver = object : BroadcastReceiver() {

		override fun onReceive(context: Context, intent: Intent) {
			val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
			Log.d(TAG,"EXTRA_RESULTS_UPDATED: $success")
			if (success)
				scanSuccess()
		}
	}

	override fun addAction() {
		val intentFilter = IntentFilter().apply {
			addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
		}
		registerReceiver(wifiScanReceiver, intentFilter)
	}

	override fun startScan() {
		@Suppress("DEPRECATION")
		if(!wifiManager.isWifiEnabled) {
			Log.d(TAG,"Enabling wifi")
			wifiManager.isWifiEnabled = true
		}
		CoroutineScope(Dispatchers.IO).launch {
			wifiManager.startScan()
			delay(1000)
			unregisterReceiver(wifiScanReceiver)
		}

	}

	@SuppressLint("MissingPermission")
	private fun scanSuccess() {
		if(isCheckedPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
			Log.d(TAG,"Results: ${wifiManager.scanResults}")
		}
	}

	companion object {
		private const val TAG = "WifiController"
	}
}