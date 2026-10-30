package com.nmichail.meridian.navigation.router.error

import com.nmichail.meridian.core.navigation.router.GlobalRouter
import com.nmichail.meridian.core.network.error.presentation.ErrorRouter
import com.nmichail.meridian.features.authorization.signin.presentation.getSignInScreen
import com.nmichail.meridian.features.authorization.workspace.presentation.getWorkspaceScreen
import javax.inject.Inject

class ErrorRouterImpl @Inject constructor(
	private val router: GlobalRouter,
) : ErrorRouter {

	override fun navigateToLogout(userBlocked: Boolean) {
		val screen = if (userBlocked) {
			getSignInScreen()
		} else {
			getWorkspaceScreen()
		}
		router.newRootScreen(screen)
	}

	override fun navigateToWorkspace() {
		router.newRootScreen(getWorkspaceScreen())
	}
}
