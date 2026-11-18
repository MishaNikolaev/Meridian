package com.nmichail.meridian.navigation.router.profile

import com.nmichail.meridian.core.navigation.router.GlobalRouter
import com.nmichail.meridian.features.authorization.workspace.presentation.getWorkspaceScreen
import com.nmichail.meridian.features.profile.presentation.ProfileRouter
import com.nmichail.meridian.features.profile.about.presentation.getAboutScreen
import javax.inject.Inject

class ProfileRouterImpl @Inject constructor(
	private val router: GlobalRouter,
) : ProfileRouter {

	override fun navigateToWorkspace() {
		router.newRootScreen(getWorkspaceScreen())
	}

	override fun navigateToAbout() {
		router.navigateTo(getAboutScreen())
	}

	override fun navigateBack() {
		router.navigateBack()
	}

	override fun openLink(url: String) {
		router.openLink(url)
	}
}
