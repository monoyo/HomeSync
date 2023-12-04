package com.jossy.wifiauthapp.controller

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.InetAddress
import java.net.MulticastSocket
import java.nio.charset.StandardCharsets

object UDPSender {

	fun makeSend(dispatcher: CoroutineDispatcher = Dispatchers.IO) {
		CoroutineScope(dispatcher).launch {
			repeat(10) {
				send()
				delay(500)
			}
		}
	}

	private fun send() {
		val message = "Hello"
	    val group = InetAddress.getByName("228.5.6.7")
		val socket = MulticastSocket(6789)
		socket.joinGroup(group)
		val bytes = message.byteInputStream(StandardCharsets.UTF_8).readBytes()
		val hi = DatagramPacket(bytes, bytes.size,
			group, 6789)
		socket.send(hi)

		val buf = ByteArray(1000)
		val recv = DatagramPacket(buf, buf.size);
		socket.receive(recv)


		socket.leaveGroup(group)

	}

}