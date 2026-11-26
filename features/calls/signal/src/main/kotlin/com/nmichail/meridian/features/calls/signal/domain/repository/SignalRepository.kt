package com.nmichail.meridian.features.calls.signal.domain.repository

import com.nmichail.meridian.features.calls.signal.domain.entity.SignalCall

interface SignalRepository {

	suspend fun sendSignal(call: SignalCall, body: String)

	suspend fun loadSignals(call: SignalCall): List<String>
}