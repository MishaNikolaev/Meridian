package com.nmichail.meridian.core.storage

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.GeneralSecurityException
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

internal class SecretCipher {

	private val key: SecretKey by lazy { loadKey() }

	fun isSealed(stored: String): Boolean = stored.startsWith(PREFIX)

	fun seal(value: String): String {
		val cipher = Cipher.getInstance(TRANSFORMATION)
		cipher.init(Cipher.ENCRYPT_MODE, key)
		val iv = cipher.iv
		check(iv != null && iv.size == IV_SIZE) { "Некорректный вектор CTR" }
		val body = cipher.doFinal(value.toByteArray(Charsets.UTF_8))
		val payload = ByteArray(iv.size + body.size)
		iv.copyInto(payload)
		body.copyInto(payload, iv.size)
		return PREFIX + Base64.encodeToString(payload, Base64.NO_WRAP)
	}

	fun open(stored: String): String? {
		val bytes = decode(stored.removePrefix(PREFIX)) ?: return null
		if (bytes.size <= IV_SIZE) {
			return null
		}
		return decrypt(bytes)
	}

	private fun decrypt(bytes: ByteArray): String? =
		try {
			val cipher = Cipher.getInstance(TRANSFORMATION)
			val iv = bytes.copyOfRange(0, IV_SIZE)
			val body = bytes.copyOfRange(IV_SIZE, bytes.size)
			cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
			String(cipher.doFinal(body), Charsets.UTF_8)
		} catch (_: GeneralSecurityException) {
			null
		}

	private fun loadKey(): SecretKey {
		val store = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
		val existing = store.getKey(KEY_ALIAS, null) as? SecretKey
		if (existing != null) {
			return existing
		}
		val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
		val purposes = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
		val spec = KeyGenParameterSpec.Builder(KEY_ALIAS, purposes)
			.setBlockModes(KeyProperties.BLOCK_MODE_CTR)
			.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
			.setKeySize(KEY_SIZE)
			.setRandomizedEncryptionRequired(true)
			.build()
		generator.init(spec)
		return generator.generateKey()
	}

	private fun decode(payload: String): ByteArray? =
		try {
			Base64.decode(payload, Base64.NO_WRAP)
		} catch (_: IllegalArgumentException) {
			null
		}

	private companion object {

		const val PREFIX = "ctr1:"
		const val KEY_ALIAS = "meridian.preferences"
		const val ANDROID_KEYSTORE = "AndroidKeyStore"
		const val TRANSFORMATION = "AES/CTR/NoPadding"
		const val KEY_SIZE = 256
		const val IV_SIZE = 16
	}
}