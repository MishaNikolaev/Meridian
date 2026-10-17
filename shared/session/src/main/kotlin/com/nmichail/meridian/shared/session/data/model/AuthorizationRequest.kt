package com.nmichail.meridian.shared.session.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthorizationRequest(
	val phraseId: String,
	val signature: String,
)
