package com.nmichail.meridian.shared.session.data.datasource

import com.nmichail.meridian.core.network.device.DeviceIdentity
import com.nmichail.meridian.core.network.device.DevicePreKey
import com.nmichail.meridian.core.network.device.DevicePreKeys
import com.nmichail.meridian.core.storage.LocalPreferences
import javax.inject.Inject

interface DeviceIdentityDataSource {

	fun read(): DeviceIdentity?

	fun write(identity: DeviceIdentity)

	fun readPreKeys(): DevicePreKeys?

	fun writePreKeys(preKeys: DevicePreKeys)

	fun clear()
}

class DeviceIdentityDataSourceImpl @Inject constructor(
	private val localPreferences: LocalPreferences,
) : DeviceIdentityDataSource {

	private companion object {

		const val DEVICE_ID = "device_identity_id"
		const val PUBLIC_KEY = "device_identity_public"
		const val PRIVATE_KEY = "device_identity_private"
		const val SIGNED_PREKEY_ID = "device_signed_prekey_id"
		const val SIGNED_PREKEY_PUBLIC = "device_signed_prekey_public"
		const val SIGNED_PREKEY_PRIVATE = "device_signed_prekey_private"
		const val SIGNED_PREKEY_SIGNATURE = "device_signed_prekey_signature"
		const val ONE_TIME_PREKEY_ID = "device_onetime_prekey_id"
		const val ONE_TIME_PREKEY_PUBLIC = "device_onetime_prekey_public"
		const val ONE_TIME_PREKEY_PRIVATE = "device_onetime_prekey_private"
	}

	override fun read(): DeviceIdentity? {
		val deviceId = localPreferences.getString(DEVICE_ID) ?: return null
		val publicKey = localPreferences.getString(PUBLIC_KEY) ?: return null
		val privateKey = localPreferences.getSecret(PRIVATE_KEY) ?: return null
		return DeviceIdentity.restore(deviceId, publicKey, privateKey)
	}

	override fun write(identity: DeviceIdentity) {
		localPreferences.putString(DEVICE_ID, identity.deviceId)
		localPreferences.putString(PUBLIC_KEY, identity.publicKey)
		localPreferences.putSecret(PRIVATE_KEY, identity.exportPrivateKey())
	}

	override fun readPreKeys(): DevicePreKeys? {
		val signedPreKey = readPreKey(SIGNED_PREKEY_ID, SIGNED_PREKEY_PUBLIC, SIGNED_PREKEY_PRIVATE) ?: return null
		val signedPreKeySignature = localPreferences.getString(SIGNED_PREKEY_SIGNATURE) ?: return null
		val oneTimePreKey = readPreKey(ONE_TIME_PREKEY_ID, ONE_TIME_PREKEY_PUBLIC, ONE_TIME_PREKEY_PRIVATE) ?: return null
		return DeviceIdentity.restorePreKeys(
			DevicePreKeys(
				signedPreKey = signedPreKey,
				signedPreKeySignature = signedPreKeySignature,
				oneTimePreKey = oneTimePreKey,
			),
		)
	}

	private fun readPreKey(idKey: String, publicKey: String, privateKey: String): DevicePreKey? {
		val id = localPreferences.getString(idKey)?.toIntOrNull() ?: return null
		val public = localPreferences.getString(publicKey) ?: return null
		val private = localPreferences.getSecret(privateKey) ?: return null
		return DevicePreKey(id, public, private)
	}

	override fun writePreKeys(preKeys: DevicePreKeys) {
		localPreferences.putString(SIGNED_PREKEY_ID, preKeys.signedPreKey.id.toString())
		localPreferences.putString(SIGNED_PREKEY_PUBLIC, preKeys.signedPreKey.publicKey)
		localPreferences.putSecret(SIGNED_PREKEY_PRIVATE, preKeys.signedPreKey.privateKey)
		localPreferences.putString(SIGNED_PREKEY_SIGNATURE, preKeys.signedPreKeySignature)
		localPreferences.putString(ONE_TIME_PREKEY_ID, preKeys.oneTimePreKey.id.toString())
		localPreferences.putString(ONE_TIME_PREKEY_PUBLIC, preKeys.oneTimePreKey.publicKey)
		localPreferences.putSecret(ONE_TIME_PREKEY_PRIVATE, preKeys.oneTimePreKey.privateKey)
	}

	override fun clear() {
		localPreferences.remove(DEVICE_ID)
		localPreferences.remove(PUBLIC_KEY)
		localPreferences.remove(PRIVATE_KEY)
		localPreferences.remove(SIGNED_PREKEY_ID)
		localPreferences.remove(SIGNED_PREKEY_PUBLIC)
		localPreferences.remove(SIGNED_PREKEY_PRIVATE)
		localPreferences.remove(SIGNED_PREKEY_SIGNATURE)
		localPreferences.remove(ONE_TIME_PREKEY_ID)
		localPreferences.remove(ONE_TIME_PREKEY_PUBLIC)
		localPreferences.remove(ONE_TIME_PREKEY_PRIVATE)
	}
}
