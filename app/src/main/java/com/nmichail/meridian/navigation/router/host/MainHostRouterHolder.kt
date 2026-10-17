package com.nmichail.meridian.navigation.router.host

import com.nmichail.meridian.features.mainhost.presentation.MainHostRouter

object MainHostRouterHolder {

	private var router: MainHostRouter? = null

	fun init(router: MainHostRouter) {
		if (this.router == null) {
			this.router = router
		}
	}

	fun get(): MainHostRouter = checkNotNull(router) { "MainHostRouter was not initialized" }
}
