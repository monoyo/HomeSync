package com.jossy.homesync.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jossy.homesync.datasource.local.data.ConnectionResult
import com.jossy.homesync.datasource.local.data.VisibleBluetoothDevice
import com.jossy.homesync.extensions.getBluetoothAdapter
import com.jossy.homesync.extensions.hasPermission
import com.jossy.homesync.extensions.registerBluetoothReceiver
import com.jossy.homesync.extensions.registerBluetoothStateReceiver
import com.jossy.homesync.extensions.unregisterBluetoothReceiver
import com.jossy.homesync.extensions.unregisterBluetoothStateReceiver
import com.jossy.homesync.presentation.MainActivity.Companion.TAG
import com.jossy.homesync.presentation.ui.theme.HomeSyncTheme
import com.jossy.homesync.viewmodel.BluetoothViewModel

class MainActivity : ComponentActivity() {

	companion object {
		const val TAG = "Bluetooth connection"
	}

	private lateinit var bluetoothViewModel: BluetoothViewModel

	private val bluetoothManager by lazy {
		applicationContext.getSystemService(BluetoothManager::class.java)
	}
	private val bluetoothAdapter by lazy {
		bluetoothManager?.adapter
	}

	private val isBluetoothEnabled: Boolean
		get() = bluetoothAdapter?.isEnabled == true

	@RequiresApi(Build.VERSION_CODES.S)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setViewModel()
		val enableBluetoothLauncher = registerForActivityResult(
			ActivityResultContracts.StartActivityForResult()
		) {

		}

		val permissionLauncher = registerForActivityResult(
			ActivityResultContracts.RequestMultiplePermissions()
		) { permissions ->
			val canEnableBluetooth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
				permissions[Manifest.permission.BLUETOOTH_CONNECT] == true
			} else {
				true
			}
			if (canEnableBluetooth && !isBluetoothEnabled) {
				enableBluetoothLauncher.launch(
					Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
				)
			}
		}

		permissionLauncher.launch(
			arrayOf(
				Manifest.permission.BLUETOOTH_SCAN,
				Manifest.permission.BLUETOOTH_CONNECT
			)
		)


		setContent {
			HomeSyncTheme {
				HomeSyncMainActivityScreen(bluetoothViewModel)
			}
		}
	}

	private fun setViewModel() {
		bluetoothViewModel = BluetoothViewModel(getBluetoothAdapter(),
			{ permission: String -> hasPermission(permission) },
			{ broadcastReceiver -> registerBluetoothReceiver(broadcastReceiver) },
			{ broadcastReceiver -> unregisterBluetoothReceiver(broadcastReceiver) },
			{ bluetoothStateReceiver -> registerBluetoothStateReceiver(bluetoothStateReceiver) },
			{ bluetoothStateReceiver -> unregisterBluetoothStateReceiver(bluetoothStateReceiver) },
			{ device, gattCallback -> connectGatt(device, gattCallback) })
	}

	@SuppressLint("MissingPermission")
	private fun connectGatt(device: BluetoothDevice, gattCallback: BluetoothGattCallback) {
		device.connectGatt(this, true, gattCallback)
	}
}

@Composable
fun HomeSyncMainActivityScreen(
	bluetoothViewModel: BluetoothViewModel
) {

	val state by bluetoothViewModel.state.collectAsState()
	val context = LocalContext.current
	LaunchedEffect(key1 = state.connectionResult) {
		when (state.connectionResult) {
			is ConnectionResult.Error -> (state.connectionResult as ConnectionResult.Error).message.let { message ->
				Toast.makeText(
					context,
					message,
					Toast.LENGTH_LONG
				).show()
				Log.e(TAG, message)
			}

			else -> {}
		}

	}
	Surface(
		modifier = Modifier.fillMaxSize(),
		color = MaterialTheme.colorScheme.background
	) {
		Column {
			Log.d(TAG,"${state.connectionResult}")
			when(state.connectionResult) {
				 ConnectionResult.Connecting -> Column(
					modifier = Modifier.fillMaxSize(),
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.Center
				) {
					CircularProgressIndicator()
					Text(text = "Connecting ...")
				}

				else -> BluetoothDeviceList(state.pairedDevices, state.scannedDevices,
					{ device: VisibleBluetoothDevice -> bluetoothViewModel.connectToDevice(device) })
			}
			Row(
				horizontalArrangement = Arrangement.Center,
				verticalAlignment = Alignment.Bottom
			) {
				when(state.connectionResult) {
					ConnectionResult.Scanning -> Button(
						onClick = { bluetoothViewModel.stopScan() },
						Modifier.padding(16.dp)
					) {
						Text(text = "Stop Scan")
					}

					else -> Button(
						onClick = { bluetoothViewModel.startScan() },
						Modifier.padding(16.dp)

					) {
						Text(text = "Start Scan")
					}
				}
			}
		}
	}
}

@Composable
fun BluetoothDeviceList(
	pairedDevices: List<VisibleBluetoothDevice>,
	scannedDevices: List<VisibleBluetoothDevice>,
	onClick: (VisibleBluetoothDevice) -> Unit,
	modifier: Modifier = Modifier
) {
	LazyColumn(modifier = modifier.fillMaxHeight(0.8f)) {
		if (pairedDevices.isNotEmpty())
			item {
				Text(
					text = "Paired Devices",
					style = MaterialTheme.typography.headlineLarge,
					modifier = Modifier.padding(16.dp)
				)
			}
		items(pairedDevices) { device ->
			Text(text = device.name ?: "Not known",
				modifier = Modifier
					.fillMaxWidth()
					.clickable { onClick(device) }
					.padding(16.dp))
		}
		if (scannedDevices.isNotEmpty())
			item {
				Text(
					text = "Scanned Devices",
					style = MaterialTheme.typography.headlineLarge,
					modifier = Modifier.padding(16.dp)
				)
			}
		items(scannedDevices) { device ->
			Text(text = device.name ?: "Not known",
				modifier = Modifier
					.fillMaxWidth()
					.clickable { onClick(device) }
					.padding(16.dp))
		}
	}
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
	Text(
		text = "Hello $name!",
		modifier = modifier
	)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
	HomeSyncTheme {
		Greeting("Android")
	}
}