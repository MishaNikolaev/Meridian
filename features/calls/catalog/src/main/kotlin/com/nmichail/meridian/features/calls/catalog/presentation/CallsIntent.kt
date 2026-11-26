package com.nmichail.meridian.features.calls.catalog.presentation

sealed interface CallsIntent {
	data object LoadInitialData : CallsIntent

	data class OpenPage(val page: CallPage) : CallsIntent

	data object OpenStart : CallsIntent

	data object OpenPlan : CallsIntent

	data object OpenFind : CallsIntent

	data class OpenMeeting(val title: String) : CallsIntent

	data object Back : CallsIntent

	data class PickDay(val index: Int) : CallsIntent
}
