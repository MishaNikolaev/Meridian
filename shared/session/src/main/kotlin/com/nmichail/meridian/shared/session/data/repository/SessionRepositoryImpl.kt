package com.nmichail.meridian.shared.session.data.repository

import com.nmichail.meridian.core.network.session.AuthSession
import com.nmichail.meridian.core.network.session.SessionStore
import com.nmichail.meridian.shared.session.data.datasource.SessionLocalDataSource
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
	private val dataSource: SessionLocalDataSource,
) : SessionStore {

	override fun isSignedIn(): Boolean = dataSource.isSignedIn()

	override fun accessToken(): String? = dataSource.accessToken()

	override fun refreshToken(): String? = dataSource.refreshToken()

	override fun save(session: AuthSession) {
		dataSource.save(session)
	}

	override fun clear() {
		dataSource.clear()
	}
}
