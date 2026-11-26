package com.nmichail.meridian.features.calls.signal.presentation

import com.nmichail.meridian.shared.calls.domain.entity.CallPhase

sealed interface CallSignalEvent {

	data class Placed(val callId: String) : CallSignalEvent

	data class Updated(val phase: CallPhase) : CallSignalEvent

	data class Signal(val body: String) : CallSignalEvent
}