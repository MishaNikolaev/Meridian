package com.nmichail.meridian.features.search.di

import com.nmichail.meridian.core.di.ComponentHolder
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.search.presentation.SearchRouter

object SearchComponentHolder : ComponentHolder<SearchFeatureApi> {

	private var component: SearchFeatureApi? = null

	fun init(core: CoreApi, router: SearchRouter) {
		if (component == null) {
			component = DaggerSearchComponent.factory().create(core, router)
		}
	}

	override fun get(): SearchFeatureApi =
		checkNotNull(component) { "SearchComponent was not initialized" }

	override fun reset() {
		component = null
	}
}
