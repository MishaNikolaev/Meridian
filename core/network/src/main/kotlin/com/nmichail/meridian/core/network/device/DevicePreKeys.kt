package com.nmichail.meridian.core.network.device

data class DevicePreKey(
	val id: Int,
	val publicKey: String,
	val privateKey: String,
)

data class DevicePreKeys(
	val signedPreKey: DevicePreKey,
	val signedPreKeySignature: String,
	val oneTimePreKey: DevicePreKey,
)
