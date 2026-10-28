package com.nmichail.meridian.features.profile.presentation

interface ProfileRouter {

	fun navigateToWorkspace()

	fun navigateToPerson(userId: String, corporate: Boolean)

	fun navigateBack()
}
