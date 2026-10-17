package com.nmichail.meridian.features.registration.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DeviceKeysRequest(
	val deviceId: String,
	val name: String,
	val identityPublicKey: String,
	val signedPreKey: SignedPreKeyRequest,
	val oneTimePreKeys: List<OneTimePreKeyRequest>,
)
