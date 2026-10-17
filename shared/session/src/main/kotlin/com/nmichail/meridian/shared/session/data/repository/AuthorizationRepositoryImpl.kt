package com.nmichail.meridian.shared.session.data.repository

import com.nmichail.meridian.core.network.session.AuthSession
import com.nmichail.meridian.core.network.device.DeviceIdentityStore
import com.nmichail.meridian.core.network.error.GatewayException
import com.nmichail.meridian.core.network.session.SessionRefresher
import com.nmichail.meridian.core.network.session.SessionStore
import com.nmichail.meridian.shared.session.data.api.AuthorizationApi
import com.nmichail.meridian.shared.session.data.mapper.toRequest
import com.nmichail.meridian.shared.session.data.model.LogoutRequest
import com.nmichail.meridian.shared.session.data.model.PasswordAuthorizationRequest
import com.nmichail.meridian.shared.session.data.model.WorkspaceAuthorizationRequest
import com.nmichail.meridian.shared.session.domain.repository.AuthorizationRepository
import javax.inject.Inject

class AuthorizationRepositoryImpl @Inject constructor(
	private val api: AuthorizationApi,
	private val deviceIdentityStore: DeviceIdentityStore,
	private val sessionStore: SessionStore,
	private val sessionRefresher: SessionRefresher,
) : AuthorizationRepository {

	override suspend fun signIn(username: String, password: String): AuthSession {
		val keys = deviceIdentityStore.identity().registrationKeys(
			deviceName = deviceIdentityStore.deviceName(),
			preKeys = deviceIdentityStore.preKeys(),
		)
		val session = api.authorizeByPassword(
			PasswordAuthorizationRequest(
				username = username.trim(),
				password = password,
				device = keys.toRequest(),
			),
		)
		sessionStore.save(session)
		return session
	}

	override suspend fun signInWorkspace(host: String, email: String, password: String): AuthSession {
		val keys = deviceIdentityStore.identity().registrationKeys(
			deviceName = deviceIdentityStore.deviceName(),
			preKeys = deviceIdentityStore.preKeys(),
		)
		val session = api.authorizeWorkspace(
			WorkspaceAuthorizationRequest(
				host = host.trim(),
				email = email.trim(),
				password = password,
				device = keys.toRequest(),
			),
		)
		sessionStore.save(session)
		return session
	}

	override suspend fun refresh(): AuthSession =
		sessionRefresher.refresh() ?: throw GatewayException(
			code = "UNAUTHORIZED",
			message = "Сессия больше не активна",
		)

	override suspend fun logout() {
		val refreshToken = sessionStore.refreshToken()
		try {
			if (!refreshToken.isNullOrBlank()) {
				api.logout(LogoutRequest(refreshToken = refreshToken))
			}
		} finally {
			sessionStore.clear()
			deviceIdentityStore.reset()
		}
	}
}
