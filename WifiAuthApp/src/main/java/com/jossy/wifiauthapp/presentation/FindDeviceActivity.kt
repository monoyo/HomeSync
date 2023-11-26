package com.jossy.wifiauthapp.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
import com.jossy.wifiauthapp.controller.WifiController
import com.jossy.wifiauthapp.controller.WifiControllerActions
import com.jossy.wifiauthapp.controller.WifiControllerI
import com.jossy.wifiauthapp.presentation.navigation.WifiAuthNavigation
import com.jossy.wifiauthapp.presentation.ui.theme.HomeSyncTheme

class FindDeviceActivity : ComponentActivity(), WifiControllerActions {
	private lateinit var wifiController: WifiControllerI
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			HomeSyncTheme {
				WifiAuthNavigation()
			}
		}
		initWifiController()
	}

	@SuppressLint("UnspecifiedRegisterReceiverFlag")
	private fun initWifiController() {
		val wifiP2pManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
		wifiController = WifiController(wifiP2pManager,
			wifiP2pManager.initialize(this, mainLooper, null),
			{ wifiScanReceiver: BroadcastReceiver, intentFilter: IntentFilter ->
				this.registerReceiver(wifiScanReceiver, intentFilter)
			},
			{
				onClickRequestPermission()
			},
			{ wifiScanReceiver: BroadcastReceiver ->
				this.unregisterReceiver(wifiScanReceiver)
			})
	}

	override fun scanDevices() {
		wifiController.startScan()
		Log.d(TAG, "Scanning devices started...")
	}

	override fun addAction() {
		wifiController.addAction()
		Log.d(TAG, "Add Action")
	}

	private fun onClickRequestPermission(): Boolean {
		var result = false
		when {
			ContextCompat.checkSelfPermission(
				this,
				Manifest.permission.ACCESS_FINE_LOCATION
			) == PackageManager.PERMISSION_GRANTED -> {
				result = true
			}

			else -> {
				requestPermissions(
					if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
						arrayOf(Manifest.permission.NEARBY_WIFI_DEVICES,
							Manifest.permission.ACCESS_FINE_LOCATION
						)
					} else {
						arrayOf(
							Manifest.permission.ACCESS_FINE_LOCATION
						)
					}
					, REQUEST_PERMISSION_NEARBY_WIFI_DEVICES_ACCESS_FINE_LOCATION
				)
				onClickRequestPermission()
			}
		}
		return result
	}

	companion object {
		private const val TAG = "FindDeviceActivity"
		private const val REQUEST_PERMISSION_NEARBY_WIFI_DEVICES_ACCESS_FINE_LOCATION = 100
	}
}