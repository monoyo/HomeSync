package com.jossy.wifiauthapp.viewmodel

import androidx.lifecycle.ViewModel
import com.jossy.wifiauthapp.domain.DeviceRepository
import com.jossy.wifiauthapp.domain.DeviceRepositoryI

class SwitchOnOffViewModel(val repository: DeviceRepositoryI = DeviceRepository()): ViewModel() {
	fun on() {
		repository.switchOn()
	}
	fun off() {
		repository.switchOff()
	}

}