package com.nmichail.meridian.core.storage

import android.content.Context
import android.util.Base64
import com.nmichail.meridian.core.di.ApplicationContext
import java.io.File
import java.security.GeneralSecurityException
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecretCipher @Inject constructor(
	@ApplicationContext context: Context,
) {

	private companion object {

		const val PREFIX = "aes1:"
		const val KEY_FILE = "meridian.aes"
		const val LEGACY_KEY_FILE = "meridian.chacha"
		const val KEY_SIZE = 32
		const val IV_SIZE = 16
		const val AES = "AES"
		const val TRANSFORMATION = "AES/CTR/NoPadding"
	}

	private val keyFile = File(context.noBackupFilesDir, KEY_FILE)
	private val random = SecureRandom()
	private val key: ByteArray by lazy { loadKey() }

	fun isSealed(stored: String): Boolean = stored.startsWith(PREFIX)

	fun seal(value: String): String {
		val iv = ByteArray(IV_SIZE)
		random.nextBytes(iv)
		val body = aes(forEncryption = true, iv = iv, input = value.toByteArray(Charsets.UTF_8))
		val payload = ByteArray(iv.size + body.size)
		iv.copyInto(payload)
		body.copyInto(payload, iv.size)
		return PREFIX + Base64.encodeToString(payload, Base64.NO_WRAP)
	}

	fun open(stored: String): String? {
		if (!isSealed(stored)) {
			return null
		}
		val bytes = decode(stored.removePrefix(PREFIX)) ?: return null
		if (bytes.size <= IV_SIZE) {
			return null
		}
		val plain = try {
			aes(
				forEncryption = false,
				iv = bytes.copyOfRange(0, IV_SIZE),
				input = bytes.copyOfRange(IV_SIZE, bytes.size),
			)
		} catch (_: GeneralSecurityException) {
			return null
		}
		return String(plain, Charsets.UTF_8)
	}

	private fun aes(forEncryption: Boolean, iv: ByteArray, input: ByteArray): ByteArray {
		val cipher = Cipher.getInstance(TRANSFORMATION)
		val mode = if (forEncryption) Cipher.ENCRYPT_MODE else Cipher.DECRYPT_MODE
		cipher.init(mode, SecretKeySpec(key, AES), IvParameterSpec(iv))
		return cipher.doFinal(input)
	}

	private fun loadKey(): ByteArray {
		readKey(keyFile)?.let { return it }
		val legacy = File(keyFile.parentFile, LEGACY_KEY_FILE)
		readKey(legacy)?.let { stored ->
			writeKey(keyFile, stored)
			legacy.delete()
			return stored
		}
		val generated = ByteArray(KEY_SIZE)
		random.nextBytes(generated)
		writeKey(keyFile, generated)
		return generated
	}

	private fun readKey(file: File): ByteArray? {
		if (!file.isFile || file.length() != KEY_SIZE.toLong()) {
			return null
		}
		val stored = file.readBytes()
		return stored.takeIf { it.size == KEY_SIZE }
	}

	private fun writeKey(file: File, bytes: ByteArray) {
		file.parentFile?.mkdirs()
		file.outputStream().use { stream -> stream.write(bytes) }
	}

	private fun decode(payload: String): ByteArray? =
		try {
			Base64.decode(payload, Base64.NO_WRAP)
		} catch (_: IllegalArgumentException) {
			null
		}
}
