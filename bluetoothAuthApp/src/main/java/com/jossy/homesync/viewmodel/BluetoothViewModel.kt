package com.jossy.homesync.viewmodel

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGattCallback
import android.content.BroadcastReceiver
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jossy.homesync.datasource.local.data.BluetoothDeviceAlias
import com.jossy.homesync.datasource.local.data.BluetoothUiState
import com.jossy.homesync.datasource.local.data.ConnectionResult
import com.jossy.homesync.datasource.local.repository.bluetooth.BluetoothController
import com.jossy.homesync.datasource.local.repository.bluetooth.BluetoothControllerI
import com.jossy.homesync.datasource.local.repository.bluetooth.receiver.BluetoothStateReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class BluetoothViewModel(
	bluetoothAdapter: BluetoothAdapter?,
	hasPermission: (String) -> Boolean,
	registerReceiver: (BroadcastReceiver) -> Intent?,
	unregisterReceiver: (BroadcastReceiver) -> Unit,
	registerBluetoothReceiver: (BluetoothStateReceiver) -> Intent?,
	unregisterBluetoothReceiver: (BluetoothStateReceiver) -> Unit,
	connectGatt: (android.bluetooth.BluetoothDevice, BluetoothGattCallback) -> Unit,
	private val bluetoothController: BluetoothControllerI = BluetoothController(
		bluetoothAdapter,
		hasPermission,
		registerReceiver,
		unregisterReceiver,
		registerBluetoothReceiver,
		unregisterBluetoothReceiver,
		connectGatt
	)
) : ViewModel() {

	init {
		bluetoothController.updatePairedDevices()
	}

	private val _state = MutableStateFlow(BluetoothUiState())

	private var deviceConnectionJob: Job? = null

	val state = combine(
		bluetoothController.scannedDevices,
		bluetoothController.pairedDevices,
		bluetoothController.connectionResult,
		_state

	) { scannedDevices, pairedDevices, connectionResult,  state ->
		state.copy(
			scannedDevices = scannedDevices,
			pairedDevices = pairedDevices,
			connectionResult = connectionResult
		)
	}.stateIn(
		viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value
	)

	fun startScan() {
		bluetoothController.startDiscovery()
		setConnectionFlow()
	}

	private fun setConnectionFlow() {
		bluetoothController.connectionResult.onEach { isConnected ->
			_state.update { it.copy(connectionResult = isConnected) }
		}.launchIn(viewModelScope)
	}

	fun stopScan() {
		bluetoothController.stopDiscovery()
	}

	fun connectToDevice(device: BluetoothDeviceAlias) {
		deviceConnectionJob = bluetoothController.connectToDevice(device, Dispatchers.IO)
			.listen()
	}

	private fun Flow<ConnectionResult>.listen(): Job = onEach { result ->
		_state.update {
			it.copy(
				connectionResult = result
			)
		}
	}.launchIn(viewModelScope)

	override fun onCleared() {
		super.onCleared()
		bluetoothController.release()
	}
}
