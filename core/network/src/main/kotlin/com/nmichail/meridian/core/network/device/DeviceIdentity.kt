package com.nmichail.meridian.core.network.device

import android.util.Base64
import java.security.SecureRandom
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.signers.Ed25519Signer

class DeviceIdentity internal constructor(
	val deviceId: String,
	val publicKey: String,
	private val privateKey: Ed25519PrivateKeyParameters,
) {

	companion object {

		fun generate(): DeviceIdentity {
			val key = generateKeyPair()
			return DeviceIdentity(
				deviceId = java.util.UUID.randomUUID().toString(),
				publicKey = key.publicKey,
				privateKey = key.privateKey,
			)
		}

		fun restorePreKeys(preKeys: DevicePreKeys): DevicePreKeys? {
			val signedMatches = keyMatches(preKeys.signedPreKey.privateKey, preKeys.signedPreKey.publicKey)
			val oneTimeMatches = keyMatches(preKeys.oneTimePreKey.privateKey, preKeys.oneTimePreKey.publicKey)
			return if (signedMatches && oneTimeMatches) preKeys else null
		}

		fun restore(deviceId: String, publicKey: String, privateKey: String): DeviceIdentity {
			val key = Ed25519PrivateKeyParameters(privateKey.decodeBase64(), 0)
			val derivedPublicKey = key.generatePublicKey().encoded.encodeBase64()
			check(derivedPublicKey == publicKey) { "Сохранённый ключ устройства повреждён" }
			return DeviceIdentity(
				deviceId = deviceId,
				publicKey = publicKey,
				privateKey = key,
			)
		}

		private fun keyMatches(privateKey: String, publicKey: String): Boolean {
			val decoded = try {
				privateKey.decodeBase64()
			} catch (_: IllegalArgumentException) {
				return false
			}
			if (decoded.size != PRIVATE_KEY_SIZE) {
				return false
			}
			val key = Ed25519PrivateKeyParameters(decoded, 0)
			return key.generatePublicKey().encoded.encodeBase64() == publicKey
		}

		private fun generateKeyPair(): GeneratedKey {
			val generator = Ed25519KeyPairGenerator()
			generator.init(Ed25519KeyGenerationParameters(SecureRandom()))
			val privateKey = generator.generateKeyPair().private as Ed25519PrivateKeyParameters
			val rawPublicKey = privateKey.generatePublicKey().encoded
			return GeneratedKey(
				privateKey = privateKey,
				rawPublicKey = rawPublicKey,
				publicKey = rawPublicKey.encodeBase64(),
			)
		}
	}

	fun sign(message: ByteArray): String {
		val signer = Ed25519Signer()
		signer.init(true, privateKey)
		signer.update(message, 0, message.size)
		return signer.generateSignature().encodeBase64()
	}

	fun signEncoded(encodedPayload: String): String = sign(encodedPayload.decodeBase64())

	fun createPreKeys(): DevicePreKeys {
		val signedPreKey = generateKeyPair()
		val oneTimePreKey = generateKeyPair()
		return DevicePreKeys(
			signedPreKey = DevicePreKey(
				id = PREKEY_ID,
				publicKey = signedPreKey.publicKey,
				privateKey = signedPreKey.privateKey.encoded.encodeBase64(),
			),
			signedPreKeySignature = sign(signedPreKey.rawPublicKey),
			oneTimePreKey = DevicePreKey(
				id = PREKEY_ID,
				publicKey = oneTimePreKey.publicKey,
				privateKey = oneTimePreKey.privateKey.encoded.encodeBase64(),
			),
		)
	}

	fun registrationKeys(deviceName: String, preKeys: DevicePreKeys): DeviceRegistrationKeys =
		DeviceRegistrationKeys(
			deviceId = deviceId,
			name = deviceName,
			identityPublicKey = publicKey,
			signedPreKeyId = preKeys.signedPreKey.id,
			signedPreKeyPublic = preKeys.signedPreKey.publicKey,
			signedPreKeySignature = preKeys.signedPreKeySignature,
			oneTimePreKeyId = preKeys.oneTimePreKey.id,
			oneTimePreKeyPublic = preKeys.oneTimePreKey.publicKey,
		)

	fun exportPrivateKey(): String = privateKey.encoded.encodeBase64()
}

private class GeneratedKey(
	val privateKey: Ed25519PrivateKeyParameters,
	val rawPublicKey: ByteArray,
	val publicKey: String,
)

private fun ByteArray.encodeBase64(): String = Base64.encodeToString(this, Base64.NO_WRAP)

private fun String.decodeBase64(): ByteArray = Base64.decode(this, Base64.NO_WRAP)

private const val PREKEY_ID = 1
private const val PRIVATE_KEY_SIZE = 32
