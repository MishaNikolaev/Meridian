package com.nmichail.meridian.shared.session.data.mapper

import com.nmichail.meridian.core.network.device.DeviceRegistrationKeys
import com.nmichail.meridian.shared.session.data.model.DeviceKeysRequest
import com.nmichail.meridian.shared.session.data.model.OneTimePreKeyRequest
import com.nmichail.meridian.shared.session.data.model.SignedPreKeyRequest

internal fun DeviceRegistrationKeys.toRequest(): DeviceKeysRequest =
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
