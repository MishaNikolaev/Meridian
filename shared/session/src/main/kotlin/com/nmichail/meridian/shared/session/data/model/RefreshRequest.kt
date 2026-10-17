package com.nmichail.meridian.shared.session.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RefreshRequest(
	val refreshToken: String,
)
