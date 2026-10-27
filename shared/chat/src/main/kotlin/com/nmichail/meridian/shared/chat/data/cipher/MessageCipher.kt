package com.nmichail.meridian.shared.chat.data.cipher

import android.util.Base64
import java.security.GeneralSecurityException
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

internal object MessageCipher {

	private val random = SecureRandom()

	fun encrypt(chatId: String, value: String): String {
		val randomBytes = ByteArray(size = 16)
		random.nextBytes(randomBytes)

		val body = aes(
			encryption = true,
			chatId = chatId,
			randomBytes = randomBytes,
			input = value.toByteArray(Charsets.UTF_8),
		)

		val encryptedBytes = ByteArray(randomBytes.size + body.size)
		randomBytes.copyInto(encryptedBytes)
		body.copyInto(encryptedBytes, randomBytes.size)
		return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
	}

	fun decrypt(chatId: String, encrypted: String): String {
		val bytes = decode(encrypted) ?: return ""
		if (bytes.size.compareTo(other = 16) <= 0) {
			return ""
		}

		val openedMessage = try {
			aes(
				encryption = false,
				chatId = chatId,
				randomBytes = bytes.copyOfRange(fromIndex = 0, toIndex = 16),
				input = bytes.copyOfRange(fromIndex = 16, toIndex = bytes.size),
			)
		} catch (_: GeneralSecurityException) {
			return ""
		}

		return String(openedMessage, Charsets.UTF_8)
	}

	private fun aes(encryption: Boolean, chatId: String, randomBytes: ByteArray, input: ByteArray): ByteArray {
		val cipher = Cipher.getInstance("AES/CTR/NoPadding")
		val mode = if (encryption) Cipher.ENCRYPT_MODE else Cipher.DECRYPT_MODE
		cipher.init(mode, SecretKeySpec(key(chatId), "AES"), IvParameterSpec(randomBytes))
		return cipher.doFinal(input)
	}

	private fun key(chatId: String): ByteArray =
		MessageDigest.getInstance("SHA-256").digest(chatId.toByteArray(Charsets.UTF_8))

	private fun decode(encoded: String): ByteArray? =
		try {
			Base64.decode(encoded, Base64.NO_WRAP)
		} catch (_: IllegalArgumentException) {
			null
		}
}