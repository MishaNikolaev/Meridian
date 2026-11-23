package com.nmichail.meridian.features.calls.signal.domain.repository

interface CallTargets {
	suspend fun deviceIds(chatId: String): List<String>
}

class CallDevicesUnavailableException : IllegalStateException()
