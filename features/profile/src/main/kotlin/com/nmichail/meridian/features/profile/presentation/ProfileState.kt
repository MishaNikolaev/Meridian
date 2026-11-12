package com.nmichail.meridian.features.profile.presentation

sealed interface ProfileState {
	data object Initial : ProfileState

	data object Loading : ProfileState

	data class Content(
		val name: String,
		val email: String,
		val status: String,
		val statusDraft: String?,
		val position: String,
		val department: String,
		val host: String,
		val photo: String,
		val installing: Boolean,
		val notice: Boolean,
	) : ProfileState

	data class Error(
		val message: Int,
	) : ProfileState
}