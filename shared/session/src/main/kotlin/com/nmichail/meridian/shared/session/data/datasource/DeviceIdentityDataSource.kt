package com.nmichail.meridian.shared.session.data.datasource

import android.content.Context
import androidx.core.content.edit
import com.nmichail.meridian.core.di.ApplicationContext
import com.nmichail.meridian.core.network.device.DeviceIdentity
import com.nmichail.meridian.core.network.device.DevicePreKey
import com.nmichail.meridian.core.network.device.DevicePreKeys
import com.nmichail.meridian.core.storage.SecretCipher
import com.nmichail.meridian.core.storage.openSecret
import com.nmichail.meridian.core.storage.putSecret
import javax.inject.Inject

interface DeviceIdentityDataSource {

	fun read(): DeviceIdentity?

	fun write(identity: DeviceIdentity)

	fun readPreKeys(): DevicePreKeys?

	fun writePreKeys(preKeys: DevicePreKeys)

	fun clear()
}

class DeviceIdentityDataSourceImpl @Inject constructor(
	@ApplicationContext context: Context,
	private val secrets: SecretCipher,
) : DeviceIdentityDataSource {

	private companion object {

		const val DEVICE_PREF = "meridian"
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

	private val preferences = context.getSharedPreferences(DEVICE_PREF, Context.MODE_PRIVATE)

	override fun read(): DeviceIdentity? {
		val deviceId = preferences.getString(DEVICE_ID, null) ?: return null
		val publicKey = preferences.getString(PUBLIC_KEY, null) ?: return null
		val privateKey = preferences.openSecret(secrets, PRIVATE_KEY) ?: return null
		return DeviceIdentity.restore(deviceId, publicKey, privateKey)
	}

	override fun write(identity: DeviceIdentity) {
		preferences.edit {
			putString(DEVICE_ID, identity.deviceId)
			putString(PUBLIC_KEY, identity.publicKey)
			putSecret(secrets, PRIVATE_KEY, identity.exportPrivateKey())
		}
	}

	override fun readPreKeys(): DevicePreKeys? {
		val signedPreKey = readPreKey(SIGNED_PREKEY_ID, SIGNED_PREKEY_PUBLIC, SIGNED_PREKEY_PRIVATE) ?: return null
		val signedPreKeySignature = preferences.getString(SIGNED_PREKEY_SIGNATURE, null) ?: return null
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
		val id = preferences.getString(idKey, null)?.toIntOrNull() ?: return null
		val public = preferences.getString(publicKey, null) ?: return null
		val private = preferences.openSecret(secrets, privateKey) ?: return null
		return DevicePreKey(id, public, private)
	}

	override fun writePreKeys(preKeys: DevicePreKeys) {
		preferences.edit {
			putString(SIGNED_PREKEY_ID, preKeys.signedPreKey.id.toString())
			putString(SIGNED_PREKEY_PUBLIC, preKeys.signedPreKey.publicKey)
			putSecret(secrets, SIGNED_PREKEY_PRIVATE, preKeys.signedPreKey.privateKey)
			putString(SIGNED_PREKEY_SIGNATURE, preKeys.signedPreKeySignature)
			putString(ONE_TIME_PREKEY_ID, preKeys.oneTimePreKey.id.toString())
			putString(ONE_TIME_PREKEY_PUBLIC, preKeys.oneTimePreKey.publicKey)
			putSecret(secrets, ONE_TIME_PREKEY_PRIVATE, preKeys.oneTimePreKey.privateKey)
		}
	}

	override fun clear() {
		preferences.edit {
			remove(DEVICE_ID)
			remove(PUBLIC_KEY)
			remove(PRIVATE_KEY)
			remove(SIGNED_PREKEY_ID)
			remove(SIGNED_PREKEY_PUBLIC)
			remove(SIGNED_PREKEY_PRIVATE)
			remove(SIGNED_PREKEY_SIGNATURE)
			remove(ONE_TIME_PREKEY_ID)
			remove(ONE_TIME_PREKEY_PUBLIC)
			remove(ONE_TIME_PREKEY_PRIVATE)
		}
	}
}
