package com.nmichail.meridian.shared.chat.data.realtime

import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.receiveAsFlow

internal suspend fun HttpClient.texts(
	host: String,
	port: Int,
	token: String,
	onText: suspend (String) -> Unit,
) {
	webSocket(
		host = host,
		port = port,
		path = "api/gateway/realtime",
		request = {
			header(HttpHeaders.Authorization, "Bearer $token")
		},
	) {
		incoming.receiveAsFlow().collect { incoming ->
			val text = incoming as? Frame.Text ?: return@collect
			onText(text.readText())
		}
	}
}