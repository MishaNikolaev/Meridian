package com.nmichail.meridian.shared.session.domain.usecase

import com.nmichail.meridian.core.network.session.AuthSession
import com.nmichail.meridian.shared.session.domain.repository.AuthorizationRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(
	private val repository: AuthorizationRepository,
) : suspend (String, String) -> AuthSession by repository::signIn
