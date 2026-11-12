package com.nmichail.meridian.features.profile.presentation

sealed interface ProfileIntent {
	data object LoadInitialData : ProfileIntent

	data object Logout : ProfileIntent

	data object StartStatus : ProfileIntent

	data class ChangeStatus(val value: String) : ProfileIntent

	data object SaveStatus : ProfileIntent

	class InstallPhoto(
		val bytes: ByteArray,
		val type: String,
	) : ProfileIntent

	data object DismissNotice : ProfileIntent
}
