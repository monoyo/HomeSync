package com.jossy.wifiauthapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.jossy.wifiauthapp.presentation.screen.SwitchOnOffScreen
import com.jossy.wifiauthapp.presentation.ui.theme.HomeSyncTheme

class DevicesActivity: ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			HomeSyncTheme {
				SwitchOnOffScreen()
			}
		}
	}
}