package com.nmichail.meridian.shared.session.domain.usecase

import com.nmichail.meridian.core.network.session.SessionRepository
import javax.inject.Inject

class GetAccessTokenUseCase @Inject constructor(
	private val sessionRepository: SessionRepository,
) : () -> String? by sessionRepository::getAccessToken
