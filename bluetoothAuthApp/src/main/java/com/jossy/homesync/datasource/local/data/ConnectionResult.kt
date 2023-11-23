package com.jossy.homesync.datasource.local.data

sealed interface ConnectionResult {
	object Connecting: ConnectionResult
	object NoConnection: ConnectionResult
	object Scanning: ConnectionResult
	data class Error(val message: String): ConnectionResult
	data class Connected(val data: ReceivedData)
}
