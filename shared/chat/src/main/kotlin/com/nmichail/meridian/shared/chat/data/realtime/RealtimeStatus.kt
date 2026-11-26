package com.nmichail.meridian.shared.chat.data.realtime

sealed interface RealtimeStatus {
	data object Unauthorized : RealtimeStatus

	data class Failed(val code: Int) : RealtimeStatus

	data object Lost : RealtimeStatus

	data object Closed : RealtimeStatus
}
