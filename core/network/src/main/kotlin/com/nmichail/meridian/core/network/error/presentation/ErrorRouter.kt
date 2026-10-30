package com.nmichail.meridian.core.network.error.presentation

interface ErrorRouter {

	fun navigateToLogout(userBlocked: Boolean)

	fun navigateToWorkspace()
}
