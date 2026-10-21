package com.nmichail.meridian.core.network.session

interface SessionRepository {

	fun isSignedIn(): Boolean

	fun getAccessToken(): String?

	fun getRefreshToken(): String?

	fun getUserId(): String?

	fun getDeviceId(): String?

	fun save(session: AuthSession)

	fun clear()
}
