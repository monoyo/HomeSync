package com.jossy.wifiauthapp.controller

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log

class WifiController(
	private val wifiManager: WifiP2pManager,
	private val channel: WifiP2pManager.Channel,
	private val registerReceiver: (BroadcastReceiver, IntentFilter) -> Intent?,
	private val isCheckedPermission: () -> Boolean,
	private val unregisterReceiver: (BroadcastReceiver) -> Any
): WifiControllerI {
	private val peers = mutableListOf<WifiP2pDevice>()

	private val peerListListener = WifiP2pManager.PeerListListener { peerList ->
		val refreshedPeers = peerList.deviceList
		if (refreshedPeers != peers) {
			peers.clear()
			peers.addAll(refreshedPeers)
			Log.d(TAG, "PEERS: $peers")
		}

		if (peers.isEmpty()) {
			Log.d(TAG, "No devices found")
			return@PeerListListener
		}
	}

	private val wifiScanReceiver = object : BroadcastReceiver() {
		@SuppressLint("MissingPermission")
		override fun onReceive(context: Context, intent: Intent) {
			when(intent.action) {
				WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
					// Determine if Wi-Fi Direct mode is enabled or not, alert
					// the Activity.
					val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
					if(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED)
						Log.d(TAG, "Wifi is On")
					else
						Log.d(TAG, "Wifi is Off")
				}
				WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
					if(isCheckedPermission())
						wifiManager.requestPeers(channel, peerListListener)
				}
				WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {

					// Connection state changed! We should probably do something about
					// that.

				}
				WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
					Log.d(TAG, "${intent.getParcelableExtra(
						WifiP2pManager.EXTRA_WIFI_P2P_DEVICE) as WifiP2pDevice?
					}")
				}
			}
		}
	}

	override fun addAction() {
		val intentFilter = IntentFilter().apply {
			// Indicates a change in the Wi-Fi Direct status.
			addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)

			// Indicates a change in the list of available peers.
			addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)

			// Indicates the state of Wi-Fi Direct connectivity has changed.
			addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)

			// Indicates this device's details have changed.
			addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
		}
		registerReceiver(wifiScanReceiver, intentFilter)
	}

	@SuppressLint("MissingPermission")
	override fun startScan() {

		if(isCheckedPermission()) {
			wifiManager.discoverPeers(channel, object: WifiP2pManager.ActionListener {
				override fun onSuccess() {
					Log.d(TAG, "Success")
				}

				override fun onFailure(p0: Int) {
					throw Error("p0")
				}
			})
		}

	}

	companion object {
		private const val TAG = "WifiController"
	}
}