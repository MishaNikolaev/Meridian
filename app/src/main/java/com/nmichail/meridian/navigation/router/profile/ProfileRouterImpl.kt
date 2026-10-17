package com.nmichail.meridian.navigation.router.profile

import com.nmichail.meridian.core.navigation.router.GlobalRouter
import com.nmichail.meridian.features.authorization.workspace.presentation.getWorkspaceScreen
import com.nmichail.meridian.features.profile.presentation.ProfileRouter
import javax.inject.Inject

class ProfileRouterImpl @Inject constructor(
	private val router: GlobalRouter,
) : ProfileRouter {

	override fun openWorkspace() {
		router.newRootScreen(getWorkspaceScreen())
	}
}
