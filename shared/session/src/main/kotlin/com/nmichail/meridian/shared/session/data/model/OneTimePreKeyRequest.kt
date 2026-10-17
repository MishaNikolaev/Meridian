package com.nmichail.meridian.shared.session.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OneTimePreKeyRequest(
	val keyId: Int,
	val publicKey: String,
)
