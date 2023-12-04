package com.jossy.wifiauthapp.domain

import com.jossy.wifiauthapp.data.SonoffStateBody
import com.jossy.wifiauthapp.domain.api.RetrofitObject
import com.jossy.wifiauthapp.domain.api.SonoffApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeviceRepository(val dispatcher: CoroutineDispatcher = Dispatchers.IO) : DeviceRepositoryI {
	private val url = "http://192.168.104.23:8081/"
	override fun switchOn() {
		val data = SonoffStateBody(
			"g1xw1a0M07npfSkP5gLn2w==",
			"10018b999a",
			true,
			"GmSgTmjXUIKF1NQPPG+tKA==",
			"123",
			"1701638835232"
		)
		CoroutineScope(dispatcher).launch {
			RetrofitObject.getInstance(url).create(SonoffApi::class.java)
				.switchState(data)
		}
	}

	override fun switchOff() {
		val data = SonoffStateBody(
			"xwyW+gS3lSAdN4u6N+jDsTlcuoUqxz/Bbj8e33r/MsA=",
			"10018b999a",
			true,
			"3QO6+x+7yzpYsbnpZ1Fshw==",
			"123",
			"1701638671484"
		)
		CoroutineScope(dispatcher).launch {
			RetrofitObject.getInstance(url).create(SonoffApi::class.java)
				.switchState(data)
		}
	}
}