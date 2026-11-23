package com.nmichail.meridian.features.profile.presentation

interface ProfileRouter {

	fun navigateToWorkspace()

	fun navigateToAbout()

	fun navigateToBusy()

	fun navigateBack()

	fun navigateToUrl(url: String)
}
