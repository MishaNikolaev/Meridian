package com.nmichail.meridian.shared.session.domain.repository

import com.nmichail.meridian.core.network.session.AuthSession

interface AuthorizationRepository {

	suspend fun signIn(username: String, password: String): AuthSession

	suspend fun signInWorkspace(host: String, email: String, password: String): AuthSession

	suspend fun refresh(): AuthSession

	suspend fun logout()
}
