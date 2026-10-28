package com.nmichail.meridian.features.profile.presentation

sealed interface ProfileIntent {

	data object LoadInitialData : ProfileIntent

	data object Logout : ProfileIntent
}
