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

	fun checkSignIn(): Boolean

	fun loadAccessToken(): String?

	fun loadRefreshToken(): String?

	fun loadUserId(): String?

	fun loadDeviceId(): String?

	fun saveSession(session: AuthSession)

	fun clearSession()
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
		const val MILLIS_IN_SECOND = 1000L
		const val EXPIRY_SKEW_MILLIS = 30_000L
	}

	private val preferences = context.getSharedPreferences(SESSION_PREF, Context.MODE_PRIVATE)

	override fun checkSignIn(): Boolean {
		val expiresAt = preferences.getString(EXPIRES_AT, null)?.toLongOrNull() ?: return false
		val accessToken = loadAccessToken()
		val fresh = expiresAt > System.currentTimeMillis() + EXPIRY_SKEW_MILLIS
		return !accessToken.isNullOrBlank() && fresh
	}

	override fun loadAccessToken(): String? =
		preferences.openSecret(secrets, ACCESS_TOKEN)?.takeIf { token -> token.isNotBlank() }

	override fun loadRefreshToken(): String? =
		preferences.openSecret(secrets, REFRESH_TOKEN)?.takeIf { token -> token.isNotBlank() }

	override fun loadUserId(): String? =
		preferences.getString(USER_ID, null)?.takeIf { id -> id.isNotBlank() }

	override fun loadDeviceId(): String? =
		preferences.getString(DEVICE_ID, null)?.takeIf { id -> id.isNotBlank() }

	override fun saveSession(session: AuthSession) {
		val expiresAt = System.currentTimeMillis() + session.expiresIn * MILLIS_IN_SECOND
		preferences.edit {
			putString(USER_ID, session.userId)
			putString(DEVICE_ID, session.deviceId)
			putSecret(secrets, ACCESS_TOKEN, session.accessToken)
			putSecret(secrets, REFRESH_TOKEN, session.refreshToken)
			putString(EXPIRES_AT, expiresAt.toString())
		}
	}

	override fun clearSession() {
		preferences.edit {
			remove(USER_ID)
			remove(DEVICE_ID)
			remove(ACCESS_TOKEN)
			remove(REFRESH_TOKEN)
			remove(EXPIRES_AT)
			remove(EXPIRES_IN)
		}
	}
}
