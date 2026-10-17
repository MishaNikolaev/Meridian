package com.nmichail.meridian.shared.session.domain.usecase

import com.nmichail.meridian.shared.session.domain.repository.AuthorizationRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
	private val repository: AuthorizationRepository,
) : suspend () -> Unit by repository::logout
