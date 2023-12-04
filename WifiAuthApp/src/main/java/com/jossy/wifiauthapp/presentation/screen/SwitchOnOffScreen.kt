package com.jossy.wifiauthapp.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jossy.wifiauthapp.viewmodel.SwitchOnOffViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

@Composable
fun SwitchOnOffScreen() {
	val viewModel: SwitchOnOffViewModel = viewModel()
	Surface(
		modifier = Modifier
			.fillMaxSize()
	) {
		var state: Boolean by remember {
			mutableStateOf(false)
		}
		val backgroundColor = if (!state) Color.Black else Color.White
		val buttonColor = if (!state) Color.Gray else Color.Green
		Column(
			modifier = Modifier
				.fillMaxSize()
				.background(backgroundColor),
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Text(
				modifier = Modifier
					.size(120.dp)
					.background(shape = RoundedCornerShape(16.dp), color = buttonColor)
					.clickable {
						if(state)
							viewModel.off()
						else
							viewModel.on()
						runBlocking {
							delay(500)
							state = !state
						}
					}.
					padding(top = 40.dp),
				text = if (state) "On" else "Off",
				fontSize = 18.sp,
				fontStyle = FontStyle.Normal,
				fontFamily = FontFamily.Monospace,
				textAlign = TextAlign.Center,
			)
		}
	}
}
