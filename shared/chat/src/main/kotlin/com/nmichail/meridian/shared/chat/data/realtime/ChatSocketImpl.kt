package com.nmichail.meridian.shared.chat.data.realtime

import com.nmichail.meridian.core.network.config.NetworkConfig
import com.nmichail.meridian.shared.chat.domain.realtime.ChatSocket
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import javax.inject.Inject

class ChatSocketImpl @Inject constructor(
	private val config: NetworkConfig,
) : ChatSocket {
	private val client = HttpClient(OkHttp) { install(WebSockets) }

	override suspend fun read(token: String, onMessage: suspend (String) -> Unit) {
		client.webSocket(
			host = config.host,
			port = config.port,
			path = "api/gateway/realtime",
			request = {
				header(HttpHeaders.Authorization, "Bearer $token")
			},
		) {
			for (frame in incoming) {
				if (frame !is Frame.Text) {
					continue
				}

				onMessage(frame.readText())
			}
		}
	}
}
