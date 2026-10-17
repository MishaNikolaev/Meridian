package com.nmichail.meridian.features.registration.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SignedPreKeyRequest(
	val keyId: Int,
	val publicKey: String,
	val signature: String,
)
