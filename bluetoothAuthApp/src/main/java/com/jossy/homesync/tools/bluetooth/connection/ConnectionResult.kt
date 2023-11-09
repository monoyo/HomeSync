package com.jossy.homesync.tools.bluetooth.connection

sealed interface ConnectionResult {
	object ConnectionEstablished: ConnectionResult
	data class Error(val message: String): ConnectionResult
}
