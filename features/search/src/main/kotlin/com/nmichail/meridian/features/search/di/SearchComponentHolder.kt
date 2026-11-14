package com.nmichail.meridian.features.search.di

import com.nmichail.meridian.core.di.ComponentHolder

object SearchComponentHolder : ComponentHolder<SearchFeatureApi> {

	private var component: SearchFeatureApi? = null

	fun init(dependencies: SearchFeatureDependencies) {
		if (component == null) {
			component = DaggerSearchComponent.factory().create(dependencies.core(), dependencies.router())
		}
	}

	override fun get(): SearchFeatureApi =
		checkNotNull(component) { "SearchComponent was not initialized" }

	override fun reset() {
		component = null
	}
}
