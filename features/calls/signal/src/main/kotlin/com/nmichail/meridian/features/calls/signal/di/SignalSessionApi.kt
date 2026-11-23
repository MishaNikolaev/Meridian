package com.nmichail.meridian.features.calls.signal.di

import com.nmichail.meridian.features.calls.signal.domain.repository.SignalRepository

interface SignalSessionApi {
	fun signalRepository(): SignalRepository
}
