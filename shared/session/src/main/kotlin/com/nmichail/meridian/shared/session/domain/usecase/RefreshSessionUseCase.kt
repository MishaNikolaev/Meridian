package com.nmichail.meridian.shared.session.domain.usecase

import com.nmichail.meridian.core.network.session.AuthSession
import com.nmichail.meridian.shared.session.domain.repository.AuthorizationRepository
import javax.inject.Inject

class RefreshSessionUseCase @Inject constructor(
	private val repository: AuthorizationRepository,
) : suspend () -> AuthSession by repository::refresh
