package com.nmichail.meridian.features.profile.presentation

sealed interface ProfileState {
	data class Initial(
		val loggingOut: Boolean,
	) : ProfileState

	data class Loading(
		val loggingOut: Boolean,
	) : ProfileState

	data class Content(
		val name: String,
		val email: String,
		val status: String,
		val statusDraft: String?,
		val position: String,
		val department: String,
		val host: String,
		val photo: String,
		val userId: String,
		val installing: Boolean,
		val savingStatus: Boolean,
		val photoSaved: Boolean,
		val loggingOut: Boolean,
	) : ProfileState

	data class Error(
		val message: Int,
		val loggingOut: Boolean,
	) : ProfileState
}
