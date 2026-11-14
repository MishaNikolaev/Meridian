package com.nmichail.meridian.shared.chat.data.realtime

import com.nmichail.meridian.core.network.config.NetworkConfig
import com.nmichail.meridian.core.network.session.SessionRefresher
import com.nmichail.meridian.core.network.session.accessTokenExpired
import com.nmichail.meridian.shared.session.domain.usecase.GetAccessTokenUseCase
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.websocket.WebSockets
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class ChatRealtime internal constructor(
	private val config: NetworkConfig,
	private val getAccessTokenUseCase: GetAccessTokenUseCase,
	private val refresher: SessionRefresher,
) {

	private val json = Json { ignoreUnknownKeys = true }
	private val client = HttpClient(OkHttp) { install(WebSockets) }
	private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
	private var restartingJob: Job? = null
	private val updates = MutableSharedFlow<String>(
		extraBufferCapacity = 16,
		onBufferOverflow = BufferOverflow.DROP_OLDEST,
	)

	val chatIds: SharedFlow<String> = updates.asSharedFlow()

	fun ensureStarted() {
		if (restartingJob?.isActive == true) {
			return
		}
		restartingJob = scope.launch {
			while (currentCoroutineContext().isActive) {
				open()
			}
		}
	}

	fun disconnect() {
		restartingJob?.cancel()

		restartingJob = null
	}

	private suspend fun open() {
		val token = accessToken()
		if (token == null) {
			delay(timeMillis = 2000L)
			return
		}
		try {
			subscribe(token)
		} catch (error: CancellationException) {
			throw error
		} catch (_: Exception) {
			delay(timeMillis = 2000L)
		}
	}

	private suspend fun accessToken(): String? {
		val current = getAccessTokenUseCase()
		if (current.isNullOrBlank()) {
			return null
		}

		if (!accessTokenExpired(current)) {
			return current
		}

		return try {
			val session = refresher.refresh()
			if (session == null) {
				refresher.expire()
				return null
			}

			session.accessToken
		} catch (error: CancellationException) {
			throw error
		} catch (_: Exception) {
			null
		}
	}

	private suspend fun subscribe(token: String) {
		client.texts(
			host = config.host,
			port = config.port,
			token = token,
		) { text ->
			val chatId = chatId(text) ?: return@texts
			updates.emit(chatId)
		}
	}

	private fun chatId(text: String): String? {
		val sync = try {
			json.decodeFromString(ChatSync.serializer(), text)
		} catch (_: SerializationException) {
			return null
		}
		return sync.event.payload.chatId?.takeIf { it.isNotBlank() }
	}
}