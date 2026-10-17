package com.nmichail.meridian.features.registration.domain.repository

import com.nmichail.meridian.core.network.session.AuthSession
import com.nmichail.meridian.features.registration.domain.entity.RegistrationData

interface RegistrationRepository {

	suspend fun register(data: RegistrationData): AuthSession
}
