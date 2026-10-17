package com.nmichail.meridian.core.network.session

import kotlinx.serialization.Serializable

@Serializable
data class AuthSession(
	val userId: String,
	val deviceId: String,
	val accessToken: String,
	val refreshToken: String,
	val expiresIn: Long,
)
