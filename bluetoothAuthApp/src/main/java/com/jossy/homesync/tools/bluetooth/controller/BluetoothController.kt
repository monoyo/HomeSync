package com.jossy.homesync.tools.bluetooth.controller

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Intent
import com.jossy.homesync.tools.bluetooth.connection.ConnectionResult
import com.jossy.homesync.tools.bluetooth.data.BluetoothDevice
import com.jossy.homesync.tools.bluetooth.data.BluetoothDeviceAlias
import com.jossy.homesync.tools.bluetooth.data.toBluetoothDeviceAlias
import com.jossy.homesync.tools.bluetooth.receiver.BluetoothStateReceiver
import com.jossy.homesync.tools.bluetooth.receiver.FoundDeviceReceiver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

@SuppressLint("MissingPermission")
class BluetoothController(
	private val bluetoothAdapter: BluetoothAdapter?,
	private val hasPermission: (String) -> Boolean,
	private val registerReceiver: (BroadcastReceiver) -> Intent?,
	private val unregisterReceiver: (BroadcastReceiver) -> Unit,
	private val registerBluetoothReceiver: (BluetoothStateReceiver) -> Intent?,
	private val unregisterBluetoothReceiver: (BluetoothStateReceiver) -> Unit,
) : BluetoothControllerI {

	private var currentServerSocket: BluetoothServerSocket? = null
	private var currentClientSocket: BluetoothSocket? = null

	private val bluetoothStateReceiver = BluetoothStateReceiver { state, bluetoothDevice ->
		if (bluetoothAdapter?.bondedDevices?.contains(bluetoothDevice) == true)
			_isConnected.update { state == android.bluetooth.BluetoothDevice.ACTION_ACL_CONNECTED }
		else {
			tryEmitError()
		}
	}

	private fun tryEmitError(dispatcher: CoroutineDispatcher = Dispatchers.IO) {
		CoroutineScope(dispatcher).launch {
			_errors.tryEmit("Can't connect to a non-paired device.")
		}
	}

	private var shouldLoop = true

	private val _isConnected = MutableStateFlow(false)

	override val isConnected: StateFlow<Boolean>
		get() = _isConnected

	private val _scannedDevices = MutableStateFlow<List<BluetoothDeviceAlias>>(emptyList())

	override val scannedDevices: StateFlow<List<BluetoothDevice>>
		get() = _scannedDevices

	private val _pairedDevices = MutableStateFlow<List<BluetoothDeviceAlias>>(emptyList())

	override val pairedDevices: StateFlow<List<BluetoothDevice>>
		get() = _pairedDevices

	private val _errors = MutableSharedFlow<String>()

	override val errors: SharedFlow<String>
		get() = _errors.asSharedFlow()

	private val foundDeviceReceiver = FoundDeviceReceiver { bluetoothDevice ->
		_scannedDevices.update { bluetoothDevices ->
			val newDevice = bluetoothDevice.toBluetoothDeviceAlias()
			if (newDevice in bluetoothDevices) bluetoothDevices else bluetoothDevices + newDevice
		}

	}

	private fun disableLoop() {
		shouldLoop = false
	}

	override fun startDiscovery() {
		if (hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
			updatePairedDevices()
			registerBluetoothReceiver(bluetoothStateReceiver)
			bluetoothAdapter?.startDiscovery()
			registerReceiver(foundDeviceReceiver)
		}
	}

	override fun stopDiscovery() {
		if (hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
			bluetoothAdapter?.cancelDiscovery()
		}
	}

	override fun startBluetoothServer(): Flow<ConnectionResult> = flow {
		if (hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
			currentServerSocket = bluetoothAdapter?.listenUsingRfcommWithServiceRecord(
				DEVICE_CONNECTION_ALIAS,
				java.util.UUID.fromString(UUID)
			)
			awaitForAcceptation().let { accepted ->
				if (accepted)
					emit(ConnectionResult.ConnectionEstablished)
			}

		}
	}.flowOn(Dispatchers.IO)

	private fun awaitForAcceptation(): Boolean {
		while (shouldLoop) {
			currentClientSocket = getAcceptedClient()
			tryCloseServer()
		}
		return true
	}

	private fun getAcceptedClient(): BluetoothSocket? = try {
		currentServerSocket?.accept()
	} catch (e: IOException) {
		disableLoop()
		null
	}

	private fun tryCloseServer() {
		currentClientSocket?.let {
			currentServerSocket?.close()
		}
	}

	override fun stopBluetoothServer() {
		currentClientSocket?.close()
		currentServerSocket?.close()
		resetClientSocket()
		resetServerSocket()
	}

	override fun connectToDevice(
		device: BluetoothDevice,
		dispatcher: CoroutineDispatcher
	): Flow<ConnectionResult> = flow {
		if (hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
			val bluetoothDevice = bluetoothAdapter?.getRemoteDevice(device.address)

			currentClientSocket = bluetoothDevice
				?.createRfcommSocketToServiceRecord(java.util.UUID.fromString(UUID))
			stopDiscovery()
			tryConnectToSocket()?.let {
				emit(it)
			}
		}
	}.onCompletion {
		stopBluetoothServer()
	}.flowOn(dispatcher)

	private fun tryConnectToSocket(): ConnectionResult? {
		var result: ConnectionResult? = null
		currentClientSocket?.let { socket ->
			result = try {
				socket.connect()
				ConnectionResult.ConnectionEstablished
			} catch (e: IOException) {
				socket.close()
				resetClientSocket()
				ConnectionResult.Error("Connection interrupted by:  $e")
			}
		}
		return result
	}

	private fun resetClientSocket() {
		currentClientSocket = null
	}

	private fun resetServerSocket() {
		currentServerSocket = null
	}


	override fun release() {
		unregisterReceiver(foundDeviceReceiver)
		stopBluetoothServer()
		unregisterBluetoothReceiver(bluetoothStateReceiver)
	}

	override fun updatePairedDevices() {
		if (hasPermission(Manifest.permission.BLUETOOTH_CONNECT))
			bluetoothAdapter?.bondedDevices?.map {
				it.toBluetoothDeviceAlias()
			}?.let { devices ->
				_pairedDevices.update { devices }
			}
	}

	companion object {
		const val DEVICE_CONNECTION_ALIAS = "device_connection_alias"
		const val UUID = "6800d926-7f46-11ee-b962-0242ac120002"
	}
}
