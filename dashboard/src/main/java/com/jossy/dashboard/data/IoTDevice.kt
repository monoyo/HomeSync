package com.jossy.dashboard.data

import androidx.compose.ui.graphics.vector.ImageVector

data class IoTDevice(
	val id: Int,
	val title: String,
	val subTitle: String,
	val vector: ImageVector,
	val itemType: ItemType
)

sealed class ItemType(type: String) {
	object Switch: ItemType(SWITCH)

	companion object {
		private const val SWITCH = "switch"
	}
}