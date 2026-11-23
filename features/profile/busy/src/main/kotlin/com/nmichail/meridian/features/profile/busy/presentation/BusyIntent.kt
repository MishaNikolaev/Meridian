package com.nmichail.meridian.features.profile.busy.presentation

sealed interface BusyIntent {
	data object Load : BusyIntent

	data object Back : BusyIntent

	data class PickDay(val index: Int) : BusyIntent

	data class Toggle(val slot: Int) : BusyIntent
}