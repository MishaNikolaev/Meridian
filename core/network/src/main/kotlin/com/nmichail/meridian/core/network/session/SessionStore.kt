package com.nmichail.meridian.core.network.session

interface SessionStore {

	fun isSignedIn(): Boolean

	fun accessToken(): String?

	fun refreshToken(): String?

	fun save(session: AuthSession)

	fun clear()
}
