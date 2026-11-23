package com.nmichail.meridian.navigation.router.busy

import com.nmichail.meridian.core.navigation.router.GlobalRouter
import com.nmichail.meridian.features.profile.busy.presentation.BusyRouter
import javax.inject.Inject

class BusyRouterImpl @Inject constructor(
	private val router: GlobalRouter,
) : BusyRouter {

	override fun navigateBack() {
		router.navigateBack()
	}
}