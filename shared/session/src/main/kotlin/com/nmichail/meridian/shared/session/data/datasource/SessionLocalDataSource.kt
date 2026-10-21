package com.nmichail.meridian.shared.session.data.datasource

import com.nmichail.meridian.core.network.session.AuthSession
import com.nmichail.meridian.core.storage.LocalPreferences
import javax.inject.Inject

interface SessionLocalDataSource {

	fun isSignedIn(): Boolean

	fun getAccessToken(): String?

	fun getRefreshToken(): String?

	fun getUserId(): String?

	fun getDeviceId(): String?

	fun save(session: AuthSession)

	fun clear()
}

class SessionLocalDataSourceImpl @Inject constructor(
	private val localPreferences: LocalPreferences,
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
		val expiresAt = localPreferences.getString(EXPIRES_AT)?.toLongOrNull() ?: return false
		return !getAccessToken().isNullOrBlank() && expiresAt > System.currentTimeMillis() + EXPIRY_SKEW_MILLIS
	}

	override fun getAccessToken(): String? = localPreferences.getSecret(ACCESS_TOKEN)?.takeIf { it.isNotBlank() }

	override fun getRefreshToken(): String? = localPreferences.getSecret(REFRESH_TOKEN)?.takeIf { it.isNotBlank() }

	override fun getUserId(): String? = localPreferences.getString(USER_ID)?.takeIf { it.isNotBlank() }

	override fun getDeviceId(): String? = localPreferences.getString(DEVICE_ID)?.takeIf { it.isNotBlank() }

	override fun save(session: AuthSession) {
		localPreferences.putString(USER_ID, session.userId)
		localPreferences.putString(DEVICE_ID, session.deviceId)
		localPreferences.putSecret(ACCESS_TOKEN, session.accessToken)
		localPreferences.putSecret(REFRESH_TOKEN, session.refreshToken)
		val expiresAt = System.currentTimeMillis() + session.expiresIn * MILLIS_IN_SECOND
		localPreferences.putString(EXPIRES_AT, expiresAt.toString())
	}

	override fun clear() {
		localPreferences.remove(USER_ID)
		localPreferences.remove(DEVICE_ID)
		localPreferences.remove(ACCESS_TOKEN)
		localPreferences.remove(REFRESH_TOKEN)
		localPreferences.remove(EXPIRES_AT)
		localPreferences.remove(EXPIRES_IN)
	}
}
