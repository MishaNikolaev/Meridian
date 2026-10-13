package com.nmichail.meridian.features.home.di

import com.nmichail.meridian.core.di.ComponentHolder

object HomeComponentHolder : ComponentHolder<HomeFeatureApi, HomeDependencies> {

	private var component: HomeFeatureApi? = null

	override fun init(dependencies: HomeDependencies) {
		if (component == null) {
			component = DaggerHomeComponent.factory().create(dependencies)
		}
	}

	override fun get(): HomeFeatureApi =
		checkNotNull(component) { "HomeComponent was not initialized" }

	override fun reset() {
		component = null
	}
}
