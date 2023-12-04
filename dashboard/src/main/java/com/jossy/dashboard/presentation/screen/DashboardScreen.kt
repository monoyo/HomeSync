package com.jossy.dashboard.presentation.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.jossy.dashboard.R
import com.jossy.dashboard.data.IoTDevice
import com.jossy.dashboard.data.ItemType
import com.jossy.dashboard.presentation.ui.theme.HomeSyncTheme
import com.jossy.dashboard.viewmodel.DashboardViewModel
import com.jossy.dashboard.viewmodel.SwitchViewModel

@Composable
fun DashboardScreen(navigation: NavHostController) {
	val viewModel: DashboardViewModel = viewModel()
	val lazyColumnScope = viewModel.devicesList
	HomeSyncTheme {
		Surface(
			modifier = Modifier
				.fillMaxSize()
		) {
			Column(modifier = Modifier
				.fillMaxSize()
				.background(MaterialTheme.colorScheme.background)
				.padding(16.dp)) {
				Text(
					text = stringResource(id = R.string.my_devices),
					fontSize = 18.sp,
					fontWeight = FontWeight.Bold,
					color = MaterialTheme.colorScheme.tertiary
				)
				Row(modifier = Modifier
					.fillMaxWidth()
					.padding(top = 16.dp)) {
					FilledTonalIconButton(onClick = { Log.d("DashboardScreen", "Clicked")}, modifier = Modifier
						.clip(CircleShape)
						.size(48.dp),
						colors = IconButtonDefaults.filledIconButtonColors(
							containerColor = MaterialTheme.colorScheme.primary,
							contentColor = MaterialTheme.colorScheme.primary
						)
					) {
						Image(imageVector = Icons.Outlined.Add, contentDescription = stringResource(
							id = R.string.add_device_button), colorFilter = ColorFilter.tint(Color.White))
					}
					if(viewModel.devicesList.isEmpty())
						Text(
							text = stringResource(id = R.string.no_devices),
							fontSize = 18.sp,
							fontWeight = FontWeight.Bold,
							modifier = Modifier.padding(start = 16.dp),
							color = MaterialTheme.colorScheme.tertiary
						)
				}
				LazyVerticalGrid(
					columns = GridCells.Adaptive(150.dp), content = {
						items(lazyColumnScope) { item ->
							when(item.itemType) {
								ItemType.Switch -> SwitchWidget(item)
							}
						}
					},
					modifier = Modifier.padding(top = 16.dp))
			}
		}
	}
}

@Composable
fun SwitchWidget(item: IoTDevice) {
	val viewModel: SwitchViewModel = viewModel()
	var checked by remember { mutableStateOf(true) }
	Card(modifier = Modifier
		.fillMaxWidth()
		.wrapContentHeight(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)) {
		Column(modifier = Modifier
			.fillMaxWidth()
			.wrapContentHeight()
			.padding(16.dp)) {
			Row(modifier = Modifier
				.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceBetween) {
				Image(imageVector = item.vector, contentDescription = stringResource(
					id = R.string.icon), colorFilter = ColorFilter.tint(Color.Black))
				Switch(checked = checked, onCheckedChange = {
					checked = it
					if(it)
						viewModel.on()
					else
						viewModel.off()
				})
			}
			Text(
				text = item.title,
				fontSize = 18.sp,
				fontWeight = FontWeight.Bold,
				color = MaterialTheme.colorScheme.tertiary,
				modifier = Modifier.padding(bottom = 8.dp)
			)
			Text(
				text = item.subTitle,
				fontSize = 14.sp,
				fontWeight = FontWeight.Light,
				color = MaterialTheme.colorScheme.tertiary
			)
		}
	}
}

@Composable
fun Silder() {
	var sliderPosition by remember { mutableFloatStateOf(0f) }
	Text(text = sliderPosition.toString())
	Slider(
		value = sliderPosition,
		onValueChange = { sliderPosition = it },
		valueRange = 0f..100f,
		onValueChangeFinished = {
			// launch some business logic update with the state you hold
			// viewModel.updateSelectedSliderValue(sliderPosition)
		},
		steps = 5,
		colors = SliderDefaults.colors(
			thumbColor = MaterialTheme.colorScheme.secondary,
			activeTrackColor = MaterialTheme.colorScheme.secondary
		)
	)
}
