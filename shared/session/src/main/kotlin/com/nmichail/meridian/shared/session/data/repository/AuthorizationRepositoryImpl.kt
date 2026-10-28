package com.nmichail.meridian.shared.session.data.repository

import com.nmichail.meridian.core.network.session.AuthSession
import com.nmichail.meridian.core.network.device.DeviceIdentityRepository
import com.nmichail.meridian.core.network.error.GatewayCode
import com.nmichail.meridian.core.network.error.GatewayException
import com.nmichail.meridian.core.network.session.SessionRefresher
import com.nmichail.meridian.core.network.session.SessionRepository
import com.nmichail.meridian.shared.session.data.api.AuthorizationApi
import com.nmichail.meridian.shared.session.data.mapper.toRequest
import com.nmichail.meridian.shared.session.data.model.LogoutRequest
import com.nmichail.meridian.shared.session.data.model.PasswordAuthorizationRequest
import com.nmichail.meridian.shared.session.data.model.WorkspaceAuthorizationRequest
import com.nmichail.meridian.shared.session.domain.repository.AuthorizationRepository
import javax.inject.Inject

class AuthorizationRepositoryImpl @Inject constructor(
	private val api: AuthorizationApi,
	private val deviceIdentityRepository: DeviceIdentityRepository,
	private val sessionRepository: SessionRepository,
	private val sessionRefresher: SessionRefresher,
) : AuthorizationRepository {

	override suspend fun signIn(username: String, password: String): AuthSession {
		val keys = deviceIdentityRepository.getIdentity().deviceKeys(
			deviceName = deviceIdentityRepository.getDeviceName(),
			preKeys = deviceIdentityRepository.getPreKeys(),
		)
		val session = api.authorizeByPassword(
			PasswordAuthorizationRequest(
				username = username.trim(),
				password = password,
				device = keys.toRequest(),
			),
		)
		sessionRepository.save(session)
		sessionRepository.saveHost("")
		sessionRepository.email("")
		return session
	}

	override suspend fun signInWorkspace(host: String, email: String, password: String): AuthSession {
		val keys = deviceIdentityRepository.getIdentity().deviceKeys(
			deviceName = deviceIdentityRepository.getDeviceName(),
			preKeys = deviceIdentityRepository.getPreKeys(),
		)
		val session = api.authorizeWorkspace(
			WorkspaceAuthorizationRequest(
				host = host.trim(),
				email = email.trim(),
				password = password,
				device = keys.toRequest(),
			),
		)
		sessionRepository.save(session)
		sessionRepository.saveHost(host.trim())
		sessionRepository.email(email.trim())
		return session
	}

	override suspend fun refresh(): AuthSession =
		sessionRefresher.refresh() ?: throw GatewayException(code = GatewayCode.UNAUTHORIZED)

	override suspend fun logout() {
		val refreshToken = sessionRepository.getRefreshToken()
		try {
			if (!refreshToken.isNullOrBlank()) {
				api.logout(LogoutRequest(refreshToken = refreshToken))
			}
		} finally {
			sessionRepository.clear()
			deviceIdentityRepository.reset()
		}
	}
}
