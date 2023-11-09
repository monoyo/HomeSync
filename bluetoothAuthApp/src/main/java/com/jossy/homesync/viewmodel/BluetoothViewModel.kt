package com.jossy.homesync.viewmodel

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jossy.homesync.datasource.local.data.BluetoothUiState
import com.jossy.homesync.tools.bluetooth.connection.ConnectionResult
import com.jossy.homesync.tools.bluetooth.controller.BluetoothController
import com.jossy.homesync.tools.bluetooth.controller.BluetoothControllerI
import com.jossy.homesync.tools.bluetooth.data.BluetoothDeviceAlias
import com.jossy.homesync.tools.bluetooth.receiver.BluetoothStateReceiver
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
	private val bluetoothController: BluetoothControllerI = BluetoothController(
		bluetoothAdapter,
		hasPermission,
		registerReceiver,
		unregisterReceiver,
		registerBluetoothReceiver,
		unregisterBluetoothReceiver
	)
) : ViewModel() {

	private val _state = MutableStateFlow(BluetoothUiState())

	private var deviceConnectionJob: Job? = null

	val state = combine(
		bluetoothController.scannedDevices,
		bluetoothController.pairedDevices,
		_state
	) { scannedDevices, pairedDevices, state ->
		state.copy(
			scannedDevices = scannedDevices,
			pairedDevices = pairedDevices
		)
	}.stateIn(
		viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value
	)

	fun startScan() {
		bluetoothController.startDiscovery()
		setConnectionFlow()
		setErrorFlow()
	}

	private fun setConnectionFlow() {
		bluetoothController.isConnected.onEach { isConnected ->
			_state.update { it.copy(isConnected = isConnected) }
		}.launchIn(viewModelScope)
	}

	private fun setErrorFlow() {
		bluetoothController.errors.onEach { error ->
			_state.update { it.copy(errorMessage = error) }
		}.launchIn(viewModelScope)
	}

	fun stopScan() {
		bluetoothController.stopDiscovery()
	}

	fun connectToDevice(device: BluetoothDeviceAlias) {
		_state.update { it.copy(isConnecting = true) }
		deviceConnectionJob = bluetoothController.connectToDevice(device, Dispatchers.IO)
			.listen()
	}

	fun disconnectFromDevice() {
		deviceConnectionJob?.cancel()
		_state.update { it.copy(isConnecting = false, isConnected = false) }

	}

	fun waitForIncomingConnections() {
		_state.update {
			it.copy(
				isConnecting = true
			)
		}
		deviceConnectionJob = bluetoothController.startBluetoothServer().listen()
	}

	fun Flow<ConnectionResult>.listen(): Job = onEach { result ->
		when(result) {
			ConnectionResult.ConnectionEstablished -> {
				_state.update {
					it.copy(
						isConnected = true,
						isConnecting = false,
						errorMessage = null
					)
				}
			}
			is ConnectionResult.Error -> {
				_state.update {
					it.copy(
						isConnected = false,
						isConnecting = false,
						errorMessage = result.message
					)
				}
			}
		}
	}.launchIn(viewModelScope)

	override fun onCleared() {
		super.onCleared()
		bluetoothController.release()
	}
}
