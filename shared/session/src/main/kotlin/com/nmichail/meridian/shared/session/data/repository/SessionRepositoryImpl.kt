package com.nmichail.meridian.shared.session.data.repository

import com.nmichail.meridian.core.network.session.AuthSession
import com.nmichail.meridian.core.network.session.SessionListener
import com.nmichail.meridian.core.network.session.SessionRepository
import com.nmichail.meridian.shared.session.data.datasource.SessionLocalDataSource
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
	private val dataSource: SessionLocalDataSource,
	private val sessionListener: SessionListener,
) : SessionRepository {

	override fun isSignedIn(): Boolean = dataSource.isSignedIn()

	override fun getAccessToken(): String? = dataSource.getAccessToken()

	override fun getRefreshToken(): String? = dataSource.getRefreshToken()

	override fun getUserId(): String? = dataSource.getUserId()

	override fun getDeviceId(): String? = dataSource.getDeviceId()

	override fun getHost(): String? = dataSource.getHost()

	override fun saveHost(host: String) {
		dataSource.saveHost(host)
	}

	override fun email(value: String?): String? = dataSource.email(value)

	override fun save(session: AuthSession) {
		dataSource.save(session)

		sessionListener.opened()
	}

	override fun clear() {
		dataSource.clear()

		sessionListener.cleared()
	}
}
