package com.nmichail.meridian.features.calls.signal.domain.repository

interface SignalSealer {
	fun encrypt(chatId: String, plain: String): String

	fun decrypt(chatId: String, sealedBody: String): String
}
