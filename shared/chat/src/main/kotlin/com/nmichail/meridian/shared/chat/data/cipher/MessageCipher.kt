package com.nmichail.meridian.shared.chat.data.cipher

import android.util.Base64
import java.security.GeneralSecurityException
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import org.bouncycastle.crypto.DataLengthException
import org.bouncycastle.crypto.InvalidCipherTextException
import org.bouncycastle.crypto.modes.ChaCha20Poly1305
import org.bouncycastle.crypto.params.KeyParameter
import org.bouncycastle.crypto.params.ParametersWithIV

internal object MessageCipher {

	private const val PREFIX = "ma1:"
	private const val CHACHA_PREFIX = "mc1:"
	private const val KEY_SIZE = 32
	private const val IV_SIZE = 16
	private const val CHACHA_NONCE_SIZE = 12
	private const val KEY_LABEL = "meridian-chat:"
	private const val AES = "AES"
	private const val TRANSFORMATION = "AES/CTR/NoPadding"

	private val random = SecureRandom()

	fun seal(chatId: String, value: String): String {
		val iv = ByteArray(IV_SIZE)
		random.nextBytes(iv)
		val body = aes(forEncryption = true, chatId = chatId, iv = iv, input = value.toByteArray(Charsets.UTF_8))
		val payload = ByteArray(iv.size + body.size)
		iv.copyInto(payload)
		body.copyInto(payload, iv.size)
		return PREFIX + Base64.encodeToString(payload, Base64.NO_WRAP)
	}

	fun open(chatId: String, stored: String): String = when {
		stored.startsWith(PREFIX) -> openAes(chatId, stored.removePrefix(PREFIX))
		stored.startsWith(CHACHA_PREFIX) -> openChaCha(chatId, stored.removePrefix(CHACHA_PREFIX))
		else -> stored
	}

	private fun openAes(chatId: String, payload: String): String {
		val bytes = decode(payload) ?: return ""
		if (bytes.size <= IV_SIZE) {
			return ""
		}
		val plain = try {
			aes(
				forEncryption = false,
				chatId = chatId,
				iv = bytes.copyOfRange(0, IV_SIZE),
				input = bytes.copyOfRange(IV_SIZE, bytes.size),
			)
		} catch (_: GeneralSecurityException) {
			return ""
		}
		return String(plain, Charsets.UTF_8)
	}

	private fun openChaCha(chatId: String, payload: String): String {
		val bytes = decode(payload) ?: return ""
		if (bytes.size <= CHACHA_NONCE_SIZE) {
			return ""
		}
		val plain = try {
			chacha(
				chatId = chatId,
				nonce = bytes.copyOfRange(0, CHACHA_NONCE_SIZE),
				input = bytes.copyOfRange(CHACHA_NONCE_SIZE, bytes.size),
			)
		} catch (_: InvalidCipherTextException) {
			return ""
		} catch (_: DataLengthException) {
			return ""
		}
		return String(plain, Charsets.UTF_8)
	}

	private fun aes(forEncryption: Boolean, chatId: String, iv: ByteArray, input: ByteArray): ByteArray {
		val cipher = Cipher.getInstance(TRANSFORMATION)
		val mode = if (forEncryption) Cipher.ENCRYPT_MODE else Cipher.DECRYPT_MODE
		cipher.init(mode, SecretKeySpec(key(chatId), AES), IvParameterSpec(iv))
		return cipher.doFinal(input)
	}

	private fun chacha(chatId: String, nonce: ByteArray, input: ByteArray): ByteArray {
		val cipher = ChaCha20Poly1305()
		cipher.init(false, ParametersWithIV(KeyParameter(key(chatId)), nonce))
		val output = ByteArray(cipher.getOutputSize(input.size))
		val written = cipher.processBytes(input, 0, input.size, output, 0)
		val total = written + cipher.doFinal(output, written)
		return output.copyOf(total)
	}

	private fun key(chatId: String): ByteArray =
		MessageDigest.getInstance("SHA-256").digest((KEY_LABEL + chatId).toByteArray(Charsets.UTF_8)).copyOf(KEY_SIZE)

	private fun decode(payload: String): ByteArray? =
		try {
			Base64.decode(payload, Base64.NO_WRAP)
		} catch (_: IllegalArgumentException) {
			null
		}
}
