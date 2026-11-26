package com.nmichail.meridian.shared.session.data.repository

import com.nmichail.meridian.core.network.session.AuthSession
import com.nmichail.meridian.core.network.session.SessionListener
import com.nmichail.meridian.core.network.session.SessionRepository
import com.nmichail.meridian.shared.session.data.datasource.SessionLocalDataSource
import com.nmichail.meridian.shared.session.data.datasource.WorkspaceLocalDataSource
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
	private val dataSource: SessionLocalDataSource,
	private val workspace: WorkspaceLocalDataSource,
	private val sessionListener: SessionListener,
) : SessionRepository {

	override fun isSignedIn(): Boolean = dataSource.checkSignIn()

	override fun getAccessToken(): String? = dataSource.loadAccessToken()

	override fun getRefreshToken(): String? = dataSource.loadRefreshToken()

	override fun getUserId(): String? = dataSource.loadUserId()

	override fun getDeviceId(): String? = dataSource.loadDeviceId()

	override fun getHost(): String? = workspace.loadHost()

	override fun saveHost(host: String) {
		workspace.saveHost(host)
	}

	override fun email(value: String?): String? {
		if (value == null) {
			return workspace.loadEmail()
		}
		return workspace.saveEmail(value)
	}

	override fun save(session: AuthSession) {
		dataSource.saveSession(session)

		sessionListener.opened()
	}

	override fun clear() {
		dataSource.clearSession()
		workspace.clearWorkspace()

		sessionListener.cleared()
	}
}
