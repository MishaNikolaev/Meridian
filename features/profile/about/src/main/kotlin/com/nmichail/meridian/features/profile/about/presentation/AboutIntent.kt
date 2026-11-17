package com.nmichail.meridian.features.profile.about.presentation

sealed interface AboutIntent {
	data object Back : AboutIntent

	data class OpenRepository(val url: String) : AboutIntent
}