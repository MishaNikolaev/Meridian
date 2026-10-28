package com.nmichail.meridian.navigation.router.registration

import com.nmichail.meridian.core.navigation.router.GlobalRouter
import com.nmichail.meridian.features.registration.presentation.RegistrationRouter
import com.nmichail.meridian.features.authorization.workspace.presentation.getWorkspaceScreen
import com.nmichail.meridian.ui.host.getMainHostScreen
import javax.inject.Inject

class RegistrationRouterImpl @Inject constructor(
	private val router: GlobalRouter,
) : RegistrationRouter {

	override fun navigateToHome() {
		router.newRootScreen(getMainHostScreen())
	}

	override fun navigateToSignIn() {
		router.navigateBack()
	}

	override fun navigateToWorkspace() {
		router.newRootScreen(getWorkspaceScreen())
	}

	override fun navigateBack() {
		router.navigateBack()
	}
}