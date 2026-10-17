package com.nmichail.meridian.navigation.router.host

import com.nmichail.meridian.core.navigation.router.GlobalRouter
import com.nmichail.meridian.features.mainhost.presentation.MainHostRouter
import javax.inject.Inject

class MainHostRouterImpl @Inject constructor(
	private val router: GlobalRouter,
) : MainHostRouter {

	override fun navigateBack() {
		router.navigateBack()
	}
}
