package com.nmichail.meridian.core.storage

import android.content.Context
import android.util.Base64
import java.io.File
import java.security.SecureRandom
import org.bouncycastle.crypto.DataLengthException
import org.bouncycastle.crypto.InvalidCipherTextException
import org.bouncycastle.crypto.modes.ChaCha20Poly1305
import org.bouncycastle.crypto.params.KeyParameter
import org.bouncycastle.crypto.params.ParametersWithIV

internal class SecretCipher(
	context: Context,
) {

	private companion object {

		const val PREFIX = "cc1:"
		const val LEGACY_PREFIX = "ctr1:"
		const val KEY_FILE = "meridian.chacha"
		const val KEY_SIZE = 32
		const val NONCE_SIZE = 12
	}

	private val keyFile = File(context.noBackupFilesDir, KEY_FILE)
	private val random = SecureRandom()
	private val key: ByteArray by lazy { loadKey() }

	fun isSealed(stored: String): Boolean = stored.startsWith(PREFIX) || stored.startsWith(LEGACY_PREFIX)

	fun seal(value: String): String {
		val nonce = ByteArray(NONCE_SIZE)
		random.nextBytes(nonce)
		val plain = value.toByteArray(Charsets.UTF_8)
		val body = crypt(forEncryption = true, nonce = nonce, input = plain)
		val payload = ByteArray(nonce.size + body.size)
		nonce.copyInto(payload)
		body.copyInto(payload, nonce.size)
		return PREFIX + Base64.encodeToString(payload, Base64.NO_WRAP)
	}

	fun open(stored: String): String? {
		if (!stored.startsWith(PREFIX)) {
			return null
		}
		val bytes = decode(stored.removePrefix(PREFIX)) ?: return null
		if (bytes.size <= NONCE_SIZE) {
			return null
		}
		val nonce = bytes.copyOfRange(0, NONCE_SIZE)
		val body = bytes.copyOfRange(NONCE_SIZE, bytes.size)
		val plain = try {
			crypt(forEncryption = false, nonce = nonce, input = body)
		} catch (_: InvalidCipherTextException) {
			return null
		} catch (_: DataLengthException) {
			return null
		} catch (_: IllegalArgumentException) {
			return null
		}
		return String(plain, Charsets.UTF_8)
	}

	private fun crypt(forEncryption: Boolean, nonce: ByteArray, input: ByteArray): ByteArray {
		val cipher = ChaCha20Poly1305()
		cipher.init(forEncryption, ParametersWithIV(KeyParameter(key), nonce))
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
