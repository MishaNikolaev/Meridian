package com.nmichail.meridian.features.registration.data.mapper

import com.nmichail.meridian.core.network.device.DeviceKeys
import com.nmichail.meridian.features.registration.data.model.DeviceKeysRequest
import com.nmichail.meridian.features.registration.data.model.OneTimePreKeyRequest
import com.nmichail.meridian.features.registration.data.model.SignedPreKeyRequest

internal fun DeviceKeys.toRequest(): DeviceKeysRequest =
	DeviceKeysRequest(
		deviceId = deviceId,
		name = name,
		identityPublicKey = identityPublicKey,
		signedPreKey = SignedPreKeyRequest(
			keyId = signedPreKeyId,
			publicKey = signedPreKeyPublic,
			signature = signedPreKeySignature,
		),
		oneTimePreKeys = listOf(
			OneTimePreKeyRequest(
				keyId = oneTimePreKeyId,
				publicKey = oneTimePreKeyPublic,
			),
		),
	)
