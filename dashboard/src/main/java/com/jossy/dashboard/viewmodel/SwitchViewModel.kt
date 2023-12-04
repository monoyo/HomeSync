package com.jossy.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import com.jossy.device.domain.DeviceRepository
import com.jossy.device.domain.DeviceRepositoryI

class SwitchViewModel(val repository: DeviceRepositoryI = DeviceRepository()): ViewModel() {
	fun on() {
		repository.switchOn()
	}
	fun off() {
		repository.switchOff()
	}

}