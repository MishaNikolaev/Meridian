package com.nmichail.meridian.shared.session.domain.usecase

import com.nmichail.meridian.core.network.session.SessionRepository
import javax.inject.Inject

class GetUserIdUseCase @Inject constructor(
	private val sessionRepository: SessionRepository,
) : () -> String? by sessionRepository::getUserId
