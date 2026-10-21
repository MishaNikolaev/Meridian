package com.nmichail.meridian.core.storage

import android.content.Context
import android.util.Base64
import java.io.File
import java.security.GeneralSecurityException
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import org.bouncycastle.crypto.DataLengthException
import org.bouncycastle.crypto.InvalidCipherTextException
import org.bouncycastle.crypto.modes.ChaCha20Poly1305
import org.bouncycastle.crypto.params.KeyParameter
import org.bouncycastle.crypto.params.ParametersWithIV

internal class SecretCipher(
	context: Context,
) {

	private companion object {

		const val PREFIX = "aes1:"
		const val CHACHA_PREFIX = "cc1:"
		const val LEGACY_PREFIX = "ctr1:"
		const val KEY_FILE = "meridian.chacha"
		const val KEY_SIZE = 32
		const val IV_SIZE = 16
		const val CHACHA_NONCE_SIZE = 12
		const val AES = "AES"
		const val TRANSFORMATION = "AES/CTR/NoPadding"
	}

	private val keyFile = File(context.noBackupFilesDir, KEY_FILE)
	private val random = SecureRandom()
	private val key: ByteArray by lazy { loadKey() }

	fun isSealed(stored: String): Boolean =
		stored.startsWith(PREFIX) || stored.startsWith(CHACHA_PREFIX) || stored.startsWith(LEGACY_PREFIX)

	fun isCurrent(stored: String): Boolean = stored.startsWith(PREFIX)

	fun seal(value: String): String {
		val iv = ByteArray(IV_SIZE)
		random.nextBytes(iv)
		val body = aes(forEncryption = true, iv = iv, input = value.toByteArray(Charsets.UTF_8))
		val payload = ByteArray(iv.size + body.size)
		iv.copyInto(payload)
		body.copyInto(payload, iv.size)
		return PREFIX + Base64.encodeToString(payload, Base64.NO_WRAP)
	}

	fun open(stored: String): String? = when {
		stored.startsWith(PREFIX) -> openAes(stored.removePrefix(PREFIX))
		stored.startsWith(CHACHA_PREFIX) -> openChaCha(stored.removePrefix(CHACHA_PREFIX))
		else -> null
	}

	private fun openAes(payload: String): String? {
		val bytes = decode(payload) ?: return null
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

	private fun openChaCha(payload: String): String? {
		val bytes = decode(payload) ?: return null
		if (bytes.size <= CHACHA_NONCE_SIZE) {
			return null
		}
		val plain = try {
			chacha(
				nonce = bytes.copyOfRange(0, CHACHA_NONCE_SIZE),
				input = bytes.copyOfRange(CHACHA_NONCE_SIZE, bytes.size),
			)
		} catch (_: InvalidCipherTextException) {
			return null
		} catch (_: DataLengthException) {
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

	private fun chacha(nonce: ByteArray, input: ByteArray): ByteArray {
		val cipher = ChaCha20Poly1305()
		cipher.init(false, ParametersWithIV(KeyParameter(key), nonce))
		val output = ByteArray(cipher.getOutputSize(input.size))
		val written = cipher.processBytes(input, 0, input.size, output, 0)
		val total = written + cipher.doFinal(output, written)
		return output.copyOf(total)
	}

	private fun loadKey(): ByteArray {
		if (keyFile.isFile && keyFile.length() == KEY_SIZE.toLong()) {
			val stored = keyFile.readBytes()
			if (stored.size == KEY_SIZE) {
				return stored
			}
		}
		val generated = ByteArray(KEY_SIZE)
		random.nextBytes(generated)
		keyFile.parentFile?.mkdirs()
		keyFile.outputStream().use { stream -> stream.write(generated) }
		return generated
	}

	private fun decode(payload: String): ByteArray? =
		try {
			Base64.decode(payload, Base64.NO_WRAP)
		} catch (_: IllegalArgumentException) {
			null
		}
}
