package com.nmichail.meridian.features.home.di

import com.nmichail.meridian.core.di.ComponentHolder
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.home.presentation.HomeRouter

object HomeComponentHolder : ComponentHolder<HomeFeatureApi> {

	private var component: HomeFeatureApi? = null

	fun init(core: CoreApi, router: HomeRouter) {
		if (component == null) {
			component = DaggerHomeComponent.factory().create(core, router)
		}
	}

	override fun get(): HomeFeatureApi =
		checkNotNull(component) { "HomeComponent was not initialized" }

	override fun reset() {
		component = null
	}
}
