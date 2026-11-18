package com.nmichail.meridian.features.profile.presentation

interface ProfileRouter {

	fun navigateToWorkspace()

	fun navigateToAbout()

	fun navigateBack()

	fun openLink(url: String)
}
