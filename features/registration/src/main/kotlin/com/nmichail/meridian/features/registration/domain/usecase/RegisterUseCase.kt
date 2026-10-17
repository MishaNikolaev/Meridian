package com.nmichail.meridian.features.registration.domain.usecase

import com.nmichail.meridian.core.network.session.AuthSession
import com.nmichail.meridian.features.registration.domain.entity.RegistrationData
import com.nmichail.meridian.features.registration.domain.repository.RegistrationRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
	private val repository: RegistrationRepository,
) : suspend (RegistrationData) -> AuthSession by repository::register
