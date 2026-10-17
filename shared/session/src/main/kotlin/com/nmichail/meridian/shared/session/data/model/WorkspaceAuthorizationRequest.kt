package com.nmichail.meridian.shared.session.data.model

import kotlinx.serialization.Serializable

@Serializable
data class WorkspaceAuthorizationRequest(
	val host: String,
	val email: String,
	val password: String,
	val device: DeviceKeysRequest,
)
