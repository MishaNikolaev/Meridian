package com.nmichail.meridian.shared.session.data.datasource

import com.nmichail.meridian.core.network.device.DeviceIdentity
import com.nmichail.meridian.core.network.device.DevicePreKey
import com.nmichail.meridian.core.network.device.DevicePreKeys
import com.nmichail.meridian.core.storage.LocalStore
import javax.inject.Inject

interface DeviceIdentityDataSource {

	fun read(): DeviceIdentity?

	fun write(identity: DeviceIdentity)

	fun readPreKeys(): DevicePreKeys?

	fun writePreKeys(preKeys: DevicePreKeys)

	fun clear()
}

class DeviceIdentityDataSourceImpl @Inject constructor(
	private val localStore: LocalStore,
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
		val deviceId = localStore.getString(DEVICE_ID) ?: return null
		val publicKey = localStore.getString(PUBLIC_KEY) ?: return null
		val privateKey = localStore.getSecret(PRIVATE_KEY) ?: return null
		return DeviceIdentity.restore(deviceId, publicKey, privateKey)
	}

	override fun write(identity: DeviceIdentity) {
		localStore.putString(DEVICE_ID, identity.deviceId)
		localStore.putString(PUBLIC_KEY, identity.publicKey)
		localStore.putSecret(PRIVATE_KEY, identity.exportPrivateKey())
	}

	override fun readPreKeys(): DevicePreKeys? {
		val signedPreKey = readPreKey(SIGNED_PREKEY_ID, SIGNED_PREKEY_PUBLIC, SIGNED_PREKEY_PRIVATE) ?: return null
		val signedPreKeySignature = localStore.getString(SIGNED_PREKEY_SIGNATURE) ?: return null
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
		val id = localStore.getString(idKey)?.toIntOrNull() ?: return null
		val public = localStore.getString(publicKey) ?: return null
		val private = localStore.getSecret(privateKey) ?: return null
		return DevicePreKey(id, public, private)
	}

	override fun writePreKeys(preKeys: DevicePreKeys) {
		localStore.putString(SIGNED_PREKEY_ID, preKeys.signedPreKey.id.toString())
		localStore.putString(SIGNED_PREKEY_PUBLIC, preKeys.signedPreKey.publicKey)
		localStore.putSecret(SIGNED_PREKEY_PRIVATE, preKeys.signedPreKey.privateKey)
		localStore.putString(SIGNED_PREKEY_SIGNATURE, preKeys.signedPreKeySignature)
		localStore.putString(ONE_TIME_PREKEY_ID, preKeys.oneTimePreKey.id.toString())
		localStore.putString(ONE_TIME_PREKEY_PUBLIC, preKeys.oneTimePreKey.publicKey)
		localStore.putSecret(ONE_TIME_PREKEY_PRIVATE, preKeys.oneTimePreKey.privateKey)
	}

	override fun clear() {
		localStore.remove(DEVICE_ID)
		localStore.remove(PUBLIC_KEY)
		localStore.remove(PRIVATE_KEY)
		localStore.remove(SIGNED_PREKEY_ID)
		localStore.remove(SIGNED_PREKEY_PUBLIC)
		localStore.remove(SIGNED_PREKEY_PRIVATE)
		localStore.remove(SIGNED_PREKEY_SIGNATURE)
		localStore.remove(ONE_TIME_PREKEY_ID)
		localStore.remove(ONE_TIME_PREKEY_PUBLIC)
		localStore.remove(ONE_TIME_PREKEY_PRIVATE)
	}
}
