package com.nmichail.meridian.features.person.presentation

sealed interface PersonIntent {

	data class Load(
		val userId: String,
		val corporate: Boolean,
	) : PersonIntent

	data object Back : PersonIntent

	data object Write : PersonIntent

	data object OpenManager : PersonIntent
}