package com.nmichail.meridian.shared.session.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PasswordAuthorizationRequest(
	val username: String,
	val password: String,
	val device: DeviceKeysRequest,
)
