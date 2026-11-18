package com.nmichail.meridian.navigation.router.about

import com.nmichail.meridian.core.navigation.router.GlobalRouter
import com.nmichail.meridian.features.profile.about.presentation.AboutRouter
import javax.inject.Inject

class AboutRouterImpl @Inject constructor(
	private val router: GlobalRouter,
) : AboutRouter {

	override fun navigateBack() {
		router.navigateBack()
	}

	override fun openLink(url: String) {
		router.openLink(url)
	}
}