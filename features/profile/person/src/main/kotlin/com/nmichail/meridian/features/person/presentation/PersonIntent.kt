package com.nmichail.meridian.features.person.presentation

sealed interface PersonIntent {

	data class Load(
		val userId: String,
		val corporate: Boolean,
	) : PersonIntent

	data object Back : PersonIntent

	data class Write(val userId: String) : PersonIntent

	data class Call(val userId: String) : PersonIntent

	data class Video(val userId: String) : PersonIntent

	data object OpenManager : PersonIntent
}