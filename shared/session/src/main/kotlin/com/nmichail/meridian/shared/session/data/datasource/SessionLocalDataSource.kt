package com.nmichail.meridian.shared.session.data.datasource

import android.content.Context
import androidx.core.content.edit
import com.nmichail.meridian.core.di.ApplicationContext
import com.nmichail.meridian.core.network.session.AuthSession
import com.nmichail.meridian.core.storage.SecretCipher
import com.nmichail.meridian.core.storage.openSecret
import com.nmichail.meridian.core.storage.putSecret
import javax.inject.Inject

interface SessionLocalDataSource {

	fun isSignedIn(): Boolean

	fun getAccessToken(): String?

	fun getRefreshToken(): String?

	fun getUserId(): String?

	fun getDeviceId(): String?

	fun getHost(): String?

	fun saveHost(host: String)

	fun email(value: String? = null): String?

	fun save(session: AuthSession)

	fun clear()
}

class SessionLocalDataSourceImpl @Inject constructor(
	@ApplicationContext context: Context,
	private val secrets: SecretCipher,
) : SessionLocalDataSource {

	private companion object {

		const val SESSION_PREF = "meridian"
		const val USER_ID = "session_user_id"
		const val DEVICE_ID = "session_device_id"
		const val ACCESS_TOKEN = "session_access_token"
		const val REFRESH_TOKEN = "session_refresh_token"
		const val EXPIRES_AT = "session_expires_at"
		const val EXPIRES_IN = "session_expires_in"
		const val HOST = "session_host"
		const val EMAIL = "session_email"
		const val MILLIS_IN_SECOND = 1000L
		const val EXPIRY_SKEW_MILLIS = 30_000L
	}

	private val preferences = context.getSharedPreferences(SESSION_PREF, Context.MODE_PRIVATE)

	override fun isSignedIn(): Boolean {
		val expiresAt = preferences.getString(EXPIRES_AT, null)?.toLongOrNull() ?: return false
		return !getAccessToken().isNullOrBlank() && expiresAt > System.currentTimeMillis() + EXPIRY_SKEW_MILLIS
	}

	override fun getAccessToken(): String? = preferences.openSecret(secrets, ACCESS_TOKEN)?.takeIf { it.isNotBlank() }

	override fun getRefreshToken(): String? = preferences.openSecret(secrets, REFRESH_TOKEN)?.takeIf { it.isNotBlank() }

	override fun getUserId(): String? = preferences.getString(USER_ID, null)?.takeIf { it.isNotBlank() }

	override fun getDeviceId(): String? = preferences.getString(DEVICE_ID, null)?.takeIf { it.isNotBlank() }

	override fun getHost(): String? = preferences.getString(HOST, null)?.takeIf { it.isNotBlank() }

	override fun saveHost(host: String) {
		val trimmed = host.trim()
		if (trimmed.isEmpty()) {
			preferences.edit { remove(HOST) }
			return
		}
		preferences.edit { putString(HOST, trimmed) }
	}

	override fun email(value: String?): String? {
		if (value == null) {
			return preferences.getString(EMAIL, null)?.takeIf { it.isNotBlank() }
		}
		val trimmed = value.trim()
		if (trimmed.isEmpty()) {
			preferences.edit { remove(EMAIL) }
			return null
		}
		preferences.edit { putString(EMAIL, trimmed) }
		return trimmed
	}

	override fun save(session: AuthSession) {
		val expiresAt = System.currentTimeMillis() + session.expiresIn * MILLIS_IN_SECOND
		preferences.edit {
			putString(USER_ID, session.userId)
			putString(DEVICE_ID, session.deviceId)
			putSecret(secrets, ACCESS_TOKEN, session.accessToken)
			putSecret(secrets, REFRESH_TOKEN, session.refreshToken)
			putString(EXPIRES_AT, expiresAt.toString())
		}
	}

	override fun clear() {
		preferences.edit {
			remove(USER_ID)
			remove(DEVICE_ID)
			remove(ACCESS_TOKEN)
			remove(REFRESH_TOKEN)
			remove(EXPIRES_AT)
			remove(EXPIRES_IN)
			remove(HOST)
			remove(EMAIL)
		}
	}
}
