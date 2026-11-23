package com.nmichail.meridian.features.profile.busy.presentation

sealed interface BusyState {
	data object Initial : BusyState

	data class Loading(
		val day: Int,
	) : BusyState

	data class Content(
		val day: Int,
		val slots: Set<Int>,
		val updating: Boolean,
	) : BusyState

	data class Error(
		val day: Int,
		val message: Int,
	) : BusyState
}