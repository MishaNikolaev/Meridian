package com.nmichail.meridian.shared.chat.domain.realtime

interface ChatSocket {
	suspend fun read(token: String, onMessage: suspend (String) -> Unit)
}
