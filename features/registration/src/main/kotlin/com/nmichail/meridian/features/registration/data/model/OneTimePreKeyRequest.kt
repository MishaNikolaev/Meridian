package com.nmichail.meridian.features.registration.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OneTimePreKeyRequest(
	val keyId: Int,
	val publicKey: String,
)
