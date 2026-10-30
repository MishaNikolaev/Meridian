package com.nmichail.meridian.features.profile.presentation

sealed interface ProfileState {

	data object Initial : ProfileState

	data object Loading : ProfileState

	data class Content(
		val name: String,
		val email: String,
	) : ProfileState

	data class Error(
		val message: Int,
	) : ProfileState
}