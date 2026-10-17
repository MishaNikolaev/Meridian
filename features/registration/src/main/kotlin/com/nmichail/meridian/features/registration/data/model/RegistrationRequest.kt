package com.nmichail.meridian.features.registration.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationRequest(
	val username: String,
	val displayName: String,
	val password: String,
	val device: DeviceKeysRequest,
)
