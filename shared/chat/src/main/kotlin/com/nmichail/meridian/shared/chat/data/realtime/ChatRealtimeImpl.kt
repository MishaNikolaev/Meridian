package com.nmichail.meridian.shared.chat.data.realtime

import com.nmichail.meridian.core.network.config.NetworkConfig
import com.nmichail.meridian.core.network.session.SessionRefresher
import com.nmichail.meridian.core.network.session.accessTokenExpired
import com.nmichail.meridian.shared.chat.domain.entity.RingNotice
import com.nmichail.meridian.shared.chat.domain.realtime.ChatRealtime
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

class ChatRealtimeImpl internal constructor(
	private val config: NetworkConfig,
	private val getAccessTokenUseCase: GetAccessTokenUseCase,
	private val refresher: SessionRefresher,
) : ChatRealtime {

	private val json = Json { ignoreUnknownKeys = true }
	private val client = HttpClient(OkHttp) { install(WebSockets) }
	private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
	private var restartingJob: Job? = null
	private val updates = MutableSharedFlow<String>(
		extraBufferCapacity = 16,
		onBufferOverflow = BufferOverflow.DROP_OLDEST,
	)
	private val rings = MutableSharedFlow<RingNotice>(
		extraBufferCapacity = 8,
		onBufferOverflow = BufferOverflow.DROP_OLDEST,
	)

	override val chatIds: SharedFlow<String> = updates.asSharedFlow()

	override val calls: SharedFlow<RingNotice> = rings.asSharedFlow()

	override fun ensureStarted() {
		if (restartingJob?.isActive == true) {
			return
		}
		restartingJob = scope.launch {
			while (currentCoroutineContext().isActive) {
				open()
			}
		}
	}

	override fun disconnect() {
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
			val ring = ring(text)
			if (ring != null) {
				rings.emit(ring)
			}
			val chatId = chatId(text) ?: return@texts
			updates.emit(chatId)
		}
	}

	private fun chatId(text: String): String? {
		val sync = frame(text) ?: return null
		if (sync.event.type == "call.state" || sync.event.type == "call.signal") {
			return null
		}
		return sync.event.payload.chatId?.takeIf { it.isNotBlank() }
	}

	private fun ring(text: String): RingNotice? {
		val sync = frame(text) ?: return null
		if (sync.event.type != "call.state") {
			return null
		}
		val payload = sync.event.payload
		val callId = payload.callId?.takeIf { it.isNotBlank() } ?: return null
		val chatId = payload.chatId?.takeIf { it.isNotBlank() } ?: return null
		val state = payload.state?.takeIf { it.isNotBlank() } ?: return null
		return RingNotice(
			callId = callId,
			chatId = chatId,
			state = state,
			actorUserId = payload.actorUserId.orEmpty(),
			media = payload.media.orEmpty(),
		)
	}

	private fun frame(text: String): ChatSync? = try {
		json.decodeFromString(ChatSync.serializer(), text)
	} catch (_: SerializationException) {
		null
	}
}