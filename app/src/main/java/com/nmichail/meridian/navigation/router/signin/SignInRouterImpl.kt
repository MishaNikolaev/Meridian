package com.nmichail.meridian.navigation.router.signin

import com.nmichail.meridian.core.navigation.router.GlobalRouter
import com.nmichail.meridian.features.authorization.signin.presentation.SignInRouter
import com.nmichail.meridian.features.authorization.workspace.presentation.getWorkspaceScreen
import com.nmichail.meridian.features.registration.presentation.getRegistrationScreen
import com.nmichail.meridian.ui.host.getMainHostScreen
import javax.inject.Inject

class SignInRouterImpl @Inject constructor(
	private val router: GlobalRouter,
) : SignInRouter {

	override fun navigateToHome() {
		router.newRootScreen(getMainHostScreen())
	}

	override fun navigateToRegistration() {
		router.navigateTo(getRegistrationScreen())
	}

	override fun navigateToWorkspace() {
		router.newRootScreen(getWorkspaceScreen())
	}

	override fun navigateBack() {
		router.navigateBack()
	}
}