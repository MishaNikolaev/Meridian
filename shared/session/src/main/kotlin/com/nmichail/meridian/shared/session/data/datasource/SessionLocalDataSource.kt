package com.nmichail.meridian.shared.session.data.datasource

import com.nmichail.meridian.core.network.session.AuthSession
import com.nmichail.meridian.core.storage.LocalStore
import javax.inject.Inject

interface SessionLocalDataSource {

	fun isSignedIn(): Boolean

	fun accessToken(): String?

	fun refreshToken(): String?

	fun save(session: AuthSession)

	fun clear()
}

class SessionLocalDataSourceImpl @Inject constructor(
	private val localStore: LocalStore,
) : SessionLocalDataSource {

	private companion object {

		const val USER_ID = "session_user_id"
		const val DEVICE_ID = "session_device_id"
		const val ACCESS_TOKEN = "session_access_token"
		const val REFRESH_TOKEN = "session_refresh_token"
		const val EXPIRES_AT = "session_expires_at"
		const val EXPIRES_IN = "session_expires_in"
		const val MILLIS_IN_SECOND = 1000L
		const val EXPIRY_SKEW_MILLIS = 30_000L
	}

	override fun isSignedIn(): Boolean {
		val expiresAt = localStore.getString(EXPIRES_AT)?.toLongOrNull() ?: return false
		return !accessToken().isNullOrBlank() && expiresAt > System.currentTimeMillis() + EXPIRY_SKEW_MILLIS
	}

	override fun accessToken(): String? = localStore.getSecret(ACCESS_TOKEN)?.takeIf { it.isNotBlank() }

	override fun refreshToken(): String? = localStore.getSecret(REFRESH_TOKEN)?.takeIf { it.isNotBlank() }

	override fun save(session: AuthSession) {
		localStore.putString(USER_ID, session.userId)
		localStore.putString(DEVICE_ID, session.deviceId)
		localStore.putSecret(ACCESS_TOKEN, session.accessToken)
		localStore.putSecret(REFRESH_TOKEN, session.refreshToken)
		val expiresAt = System.currentTimeMillis() + session.expiresIn * MILLIS_IN_SECOND
		localStore.putString(EXPIRES_AT, expiresAt.toString())
	}

	override fun clear() {
		localStore.remove(USER_ID)
		localStore.remove(DEVICE_ID)
		localStore.remove(ACCESS_TOKEN)
		localStore.remove(REFRESH_TOKEN)
		localStore.remove(EXPIRES_AT)
		localStore.remove(EXPIRES_IN)
	}
}
