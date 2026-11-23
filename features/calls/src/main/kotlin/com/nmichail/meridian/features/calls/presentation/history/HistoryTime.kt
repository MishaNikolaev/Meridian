package com.nmichail.meridian.features.calls.presentation.history

sealed interface HistoryTime {
	data class Clock(
		val hour: Int,
		val minute: Int,
	) : HistoryTime

	data object Yesterday : HistoryTime

	data class Day(
		val day: Int,
		val month: Int,
	) : HistoryTime

	data object Unknown : HistoryTime
}
