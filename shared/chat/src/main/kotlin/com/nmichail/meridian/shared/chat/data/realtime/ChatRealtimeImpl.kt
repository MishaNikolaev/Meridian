package com.nmichail.meridian.shared.chat.data.realtime

import com.nmichail.meridian.core.network.session.SessionRefresher
import com.nmichail.meridian.core.network.session.accessTokenExpired
import com.nmichail.meridian.shared.chat.domain.entity.RingNotice
import com.nmichail.meridian.shared.chat.domain.realtime.ChatRealtime
import com.nmichail.meridian.shared.chat.domain.realtime.ChatSocket
import com.nmichail.meridian.shared.session.domain.usecase.GetAccessTokenUseCase
import io.ktor.client.plugins.ResponseException
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

class ChatRealtimeImpl internal constructor(
	private val socket: ChatSocket,
	private val getAccessTokenUseCase: GetAccessTokenUseCase,
	private val refresher: SessionRefresher,
) : ChatRealtime {
	private val json = Json {
		ignoreUnknownKeys = true
		classDiscriminator = "type"
	}
	private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
	private var readingJob: Job? = null
	private val updates = MutableSharedFlow<String>(
		extraBufferCapacity = 16,
		onBufferOverflow = BufferOverflow.DROP_OLDEST,
	)
	private val rings = MutableSharedFlow<RingNotice>(
		extraBufferCapacity = 8,
		onBufferOverflow = BufferOverflow.DROP_OLDEST,
	)
	private val statuses = MutableSharedFlow<RealtimeStatus>(
		extraBufferCapacity = 8,
		onBufferOverflow = BufferOverflow.DROP_OLDEST,
	)

	override val chatIds: SharedFlow<String> = updates.asSharedFlow()

	override val calls: SharedFlow<RingNotice> = rings.asSharedFlow()

	val status: SharedFlow<RealtimeStatus> = statuses.asSharedFlow()

	override fun ensureStarted() {
		if (readingJob?.isActive == true) {
			return
		}

		readingJob = scope.launch { readSocket() }
	}

	override fun disconnect() {
		readingJob?.cancel()
		readingJob = null
	}

	private suspend fun readSocket() {
		val token = loadAccessToken()

		if (token == null) {
			statuses.emit(RealtimeStatus.Unauthorized)
			return
		}

		try {
			socket.read(token) { text ->
				val ring = ring(text)

				if (ring != null) {
					rings.emit(ring)
				}

				val chatId = chatId(text) ?: return@read

				updates.emit(chatId)
			}
			statuses.emit(RealtimeStatus.Closed)
		} catch (error: CancellationException) {
			throw error
		} catch (error: ResponseException) {
			statuses.emit(RealtimeStatus.Failed(error.response.status.value))
		} catch (_: Exception) {
			statuses.emit(RealtimeStatus.Lost)
		}
	}

	private suspend fun loadAccessToken(): String? {
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

	private fun chatId(text: String): String? {
		val sync = (decode(text) as? IncomingMessage.Sync) ?: return null

		if (sync.event.type == "call.state" || sync.event.type == "call.signal") {
			return null
		}

		return sync.event.payload.text("chatId")
	}

	private fun ring(text: String): RingNotice? {
		val sync = (decode(text) as? IncomingMessage.Sync) ?: return null

		if (sync.event.type != "call.state") {
			return null
		}

		val payload = sync.event.payload
		val callId = payload.text("callId") ?: return null
		val chatId = payload.text("chatId") ?: return null
		val state = payload.text("state") ?: return null

		return RingNotice(
			callId = callId,
			chatId = chatId,
			state = state,
			actorUserId = payload.text("actorUserId").orEmpty(),
			media = payload.text("media").orEmpty(),
		)
	}

	private fun decode(text: String): IncomingMessage? = try {
		json.decodeFromString(IncomingMessage.serializer(), text)
	} catch (_: SerializationException) {
		null
	}
}

private fun JsonObject.text(key: String): String? =
	(this[key] as? JsonPrimitive)?.contentOrNull?.takeIf { value -> value.isNotBlank() }
