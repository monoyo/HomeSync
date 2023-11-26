package com.jossy.wifiauthapp.presentation

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
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
		val wifiManager = this.getSystemService(Context.WIFI_SERVICE) as WifiManager
		wifiController = WifiController(wifiManager,
			{ wifiScanReceiver: BroadcastReceiver, intentFilter: IntentFilter ->
				this.registerReceiver(wifiScanReceiver, intentFilter)
			},
			{ permission: String ->
				ActivityCompat.checkSelfPermission(
					this,
					permission
				) != PackageManager.PERMISSION_GRANTED
			}) { wifiScanReceiver: BroadcastReceiver ->
			this.unregisterReceiver(wifiScanReceiver)
		}
	}

	override fun scanDevices() {
		wifiController.startScan()
		Log.d(TAG, "Scanning devices started...")
	}

	override fun addAction() {
		wifiController.addAction()
		Log.d(TAG, "Add Action")
	}

	companion object {
		private const val TAG = "FindDeviceActivity"
	}
}