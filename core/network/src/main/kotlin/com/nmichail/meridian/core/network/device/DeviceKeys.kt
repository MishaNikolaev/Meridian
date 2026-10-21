package com.nmichail.meridian.core.network.device

data class DeviceKeys(
	val deviceId: String,
	val name: String,
	val identityPublicKey: String,
	val signedPreKeyId: Int,
	val signedPreKeyPublic: String,
	val signedPreKeySignature: String,
	val oneTimePreKeyId: Int,
	val oneTimePreKeyPublic: String,
)
