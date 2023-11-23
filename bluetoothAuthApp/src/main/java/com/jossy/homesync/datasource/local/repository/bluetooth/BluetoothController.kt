package com.jossy.homesync.datasource.local.repository.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Intent
import android.util.Log
import com.jossy.homesync.datasource.local.data.BluetoothDeviceAlias
import com.jossy.homesync.datasource.local.data.ConnectionResult
import com.jossy.homesync.datasource.local.data.VisibleBluetoothDevice
import com.jossy.homesync.datasource.local.data.toBluetoothDeviceAlias
import com.jossy.homesync.datasource.local.repository.bluetooth.receiver.BluetoothStateReceiver
import com.jossy.homesync.datasource.local.repository.bluetooth.receiver.FoundDeviceReceiver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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
	private val connectGatt: (BluetoothDevice, BluetoothGattCallback) -> Unit
) : BluetoothControllerI {

	private var currentClientSocket: BluetoothSocket? = null

	private var gatt: BluetoothGatt? = null

	private val bleSnanner by lazy {
		bluetoothAdapter?.bluetoothLeScanner
	}

	private var isScanning = false

	private val bluetoothStateReceiver = BluetoothStateReceiver { state, bluetoothDevice ->
	/*	if (bluetoothAdapter?.bondedDevices?.contains(bluetoothDevice) == true)
			_connectionResult.update { state == BluetoothDevice.ACTION_ACL_CONNECTED }
		else {
			tryEmitError()
		}*/
	}

	private fun tryEmitError(dispatcher: CoroutineDispatcher = Dispatchers.IO) {
		CoroutineScope(dispatcher).launch {
			_errors.tryEmit("Can't connect to a non-paired device.")
		}
	}

	private fun setToScanning(dispatcher: CoroutineDispatcher = Dispatchers.IO) {
		CoroutineScope(dispatcher).launch {
			_connectionResult.tryEmit(ConnectionResult.Scanning)
		}
	}
	private fun setToNoConnection(dispatcher: CoroutineDispatcher = Dispatchers.IO) {
		CoroutineScope(dispatcher).launch {
			_connectionResult.tryEmit(ConnectionResult.NoConnection)
		}
	}


	private val _connectionResult = MutableStateFlow<ConnectionResult>(
		ConnectionResult.NoConnection
	)

	override val connectionResult: StateFlow<ConnectionResult>
		get() = _connectionResult

	private val _scannedDevices = MutableStateFlow<List<BluetoothDeviceAlias>>(emptyList())

	override val scannedDevices: StateFlow<List<VisibleBluetoothDevice>>
		get() = _scannedDevices

	private val _pairedDevices = MutableStateFlow<List<BluetoothDeviceAlias>>(emptyList())

	override val pairedDevices: StateFlow<List<VisibleBluetoothDevice>>
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

	override fun startDiscovery() {
		if (hasPermission(Manifest.permission.BLUETOOTH_SCAN) && !isScanning) {
			registerBluetoothReceiver(bluetoothStateReceiver)
			bluetoothAdapter?.startDiscovery()
			registerReceiver(foundDeviceReceiver)
			isScanning = true
			setToScanning()
		}
	}

	override fun stopDiscovery() {
		if (hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
			bluetoothAdapter?.cancelDiscovery()
			setToNoConnection()
			isScanning = false
		}
	}

	private var once = false

	private val bluetoothGattCallback = object : BluetoothGattCallback() {
		override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
			super.onConnectionStateChange(gatt, status, newState)
			if(!once) {
				Log.d(TAG, "Device connected: ${gatt.connect()}")
				Log.d(TAG, "Device new state: $newState")
				Log.d(TAG, "Status: $status")
				gatt.discoverServices()
				CoroutineScope(Dispatchers.IO).launch {
					delay(5000)
					manageGattServices(gatt)
					delay(1000)
				}
				once = true

			}
		}

		override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
			super.onServicesDiscovered(gatt, status)
			gatt?.let {
				adjustMTU(it)
			}
		}

		override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
			super.onMtuChanged(gatt, mtu, status)

		}

		override fun onCharacteristicChanged(
			gatt: BluetoothGatt,
			characteristic: BluetoothGattCharacteristic,
			value: ByteArray
		) {
			super.onCharacteristicChanged(gatt, characteristic, value)
			Log.d(TAG, "$value")
		}
	}

	private fun manageGattServices(gatt: BluetoothGatt) {
		Log.d(TAG,"services: ___--__--___----_--_----")
		gatt.services.forEachIndexed { position, service ->
			Log.d(TAG, "Service $position characteristics:")
			service.characteristics.forEachIndexed { positionCH, bluetoothGattCharacteristic ->
				Log.d(TAG, "Service $positionCH service type: ${bluetoothGattCharacteristic.service.type}")
				Log.d(TAG, "Service $positionCH service characteristics size: ${bluetoothGattCharacteristic.service.characteristics.size}")
				Log.d(TAG, "Service $positionCH descriptors: ${bluetoothGattCharacteristic.descriptors}")
				Log.d(TAG, "Service $positionCH instanceId: ${bluetoothGattCharacteristic.instanceId}")
				Log.d(TAG, "Service $positionCH uuid: ${bluetoothGattCharacteristic.uuid}")
				Log.d(TAG, "Service $positionCH permissions: ${bluetoothGattCharacteristic.permissions}")
				Log.d(TAG, "Service $positionCH properties: ${bluetoothGattCharacteristic.properties}")
			}
			Log.d(TAG, "Service $position includedServices: ${service.includedServices}")
			Log.d(TAG, "Service $position instanceId: ${service.instanceId}")
			Log.d(TAG, "Service $position type: ${service.type}")
			Log.d(TAG, "Service $position uuid: ${service.uuid}")

		}
	}

	private fun adjustMTU(gatt: BluetoothGatt) {
		gatt.requestMtu(MTU)
	}

	override fun connectToDevice(
		device: VisibleBluetoothDevice,
		dispatcher: CoroutineDispatcher
	): Flow<ConnectionResult> = flow {
		if (hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
			val bluetoothDevice = bluetoothAdapter?.getRemoteDevice(device.address)
			bluetoothDevice?.let { device ->
				connectGatt(device, bluetoothGattCallback)
			}
			stopDiscovery()
			tryConnectToSocket()?.let {
				emit(it)
			}
		}
	}.flowOn(dispatcher)

	private fun tryConnectToSocket(): ConnectionResult? {
		var result: ConnectionResult? = null
		currentClientSocket?.let { socket ->
			result = try {
				socket.connect()
				ConnectionResult.Connecting
			} catch (e: IOException) {
				socket.close()
				resetClientSocket()
				ConnectionResult.Error("Connection interrupted by:  $e")
			}
		}
		return result
	}


	override fun release() {
		unregisterReceiver(foundDeviceReceiver)
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

	private fun resetClientSocket() {
		currentClientSocket = null
	}

	companion object {
		const val MTU = 517
		const val TAG = "Bluetooth Controller"
	}
}
