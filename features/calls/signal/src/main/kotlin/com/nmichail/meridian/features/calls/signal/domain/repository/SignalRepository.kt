package com.nmichail.meridian.features.calls.signal.domain.repository

interface SignalRepository {

	suspend fun send(callId: String, chatId: String, body: String)

	suspend fun load(callId: String, chatId: String): List<String>
}
