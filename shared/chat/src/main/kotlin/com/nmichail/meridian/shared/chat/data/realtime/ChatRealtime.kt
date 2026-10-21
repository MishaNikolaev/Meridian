package com.nmichail.meridian.shared.chat.data.realtime

import com.nmichail.meridian.core.di.PerFeature
import com.nmichail.meridian.core.network.config.NetworkConfig
import com.nmichail.meridian.shared.chat.data.model.RealtimeFrame
import com.nmichail.meridian.shared.session.domain.usecase.GetAccessTokenUseCase
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.coroutines.cancellation.CancellationException

@PerFeature
class ChatRealtime @Inject constructor(
	private val config: NetworkConfig,
	private val getAccessTokenUseCase: GetAccessTokenUseCase,
	private val json: Json,
) {

	private val client = HttpClient(OkHttp) { install(WebSockets) }
	private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
	private var loopJob: Job? = null
	private val updates = MutableSharedFlow<String>()

	val chatIds: SharedFlow<String> = updates.asSharedFlow()

	fun ensureStarted() {
		if (loopJob?.isActive == true) {
			return
		}
		loopJob = scope.launch { loop() }
	}

	private suspend fun loop() {
		while (scope.isActive) {
			val token = getAccessTokenUseCase()
			if (token.isNullOrBlank()) {
				continue
			}
			try {
				connect(token)
			} catch (error: CancellationException) {
				throw error
			} catch (_: Exception) {
			}
		}
	}

	private suspend fun connect(token: String) {
		client.webSocket(
			host = config.host,
			port = config.port,
			path = "api/gateway/realtime",
			request = {
				header(HttpHeaders.Authorization, "Bearer $token")
			},
		) {
			for (frame in incoming) {
				read(frame)
			}
		}
	}

	private suspend fun read(frame: Frame) {
		val text = frame as? Frame.Text ?: return
		val chatId = chatId(text.readText()) ?: return
		updates.emit(chatId)
	}

	private fun chatId(text: String): String? {
		val frame = try {
			json.decodeFromString(RealtimeFrame.serializer(), text)
		} catch (_: SerializationException) {
			return null
		}
		val sync = frame as? RealtimeFrame.Sync ?: return null
		return sync.event.payload.chatId?.takeIf { it.isNotBlank() }
	}
}
