package com.nmichail.meridian.features.registration.data.api

import com.nmichail.meridian.core.network.session.AuthSession
import com.nmichail.meridian.features.registration.data.model.RegistrationRequest
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface RegistrationApi {

	@POST("api/gateway/registration")
	suspend fun register(@Body request: RegistrationRequest): AuthSession
}
