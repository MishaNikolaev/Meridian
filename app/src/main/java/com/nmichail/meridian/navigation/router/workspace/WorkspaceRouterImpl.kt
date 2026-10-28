package com.nmichail.meridian.navigation.router.workspace

import com.nmichail.meridian.core.navigation.router.GlobalRouter
import com.nmichail.meridian.features.authorization.signin.presentation.getSignInScreen
import com.nmichail.meridian.features.authorization.workspace.presentation.WorkspaceRouter
import com.nmichail.meridian.ui.host.getMainHostScreen
import javax.inject.Inject

class WorkspaceRouterImpl @Inject constructor(
	private val router: GlobalRouter,
) : WorkspaceRouter {

	override fun navigateToHome() {
		router.newRootScreen(getMainHostScreen())
	}

	override fun navigateToSignIn() {
		router.navigateTo(getSignInScreen())
	}

	override fun navigateBack() {
		router.navigateBack()
	}
}