package com.nmichail.meridian.core.network.session

import com.nmichail.meridian.core.network.config.NetworkConfig
import com.nmichail.meridian.core.network.device.DeviceIdentityRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Singleton
class SessionRefresher @Inject constructor(
	private val sessionRepository: SessionRepository,
	private val deviceIdentityRepository: DeviceIdentityRepository,
	private val config: NetworkConfig,
	private val json: Json,
	private val events: SessionEvents,
) {

	private val lock = Mutex()

	private val client = HttpClient(OkHttp) {
		expectSuccess = false
		install(ContentNegotiation) {
			json(json)
		}
	}

	suspend fun refresh(): AuthSession? = lock.withLock {
		val refreshToken = sessionRepository.getRefreshToken() ?: return null
		val response = client.post("${config.baseUrl}api/gateway/authorization/refresh") {
			contentType(ContentType.Application.Json)
			setBody(RefreshBody(refreshToken))
		}
		if (!response.status.isSuccess()) {
			return null
		}
		val session = response.body<AuthSession>()
		sessionRepository.save(session)
		session
	}

	fun expire() {
		val signedIn = sessionRepository.getAccessToken() != null || sessionRepository.getRefreshToken() != null
		sessionRepository.clear()
		deviceIdentityRepository.reset()
		if (signedIn) {
			events.notifyExpired()
		}
	}
}

@Serializable
private data class RefreshBody(
	val refreshToken: String,
)
