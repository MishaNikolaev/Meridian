package com.nmichail.meridian.features.groups.di

import com.nmichail.meridian.core.di.ComponentHolder
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.groups.presentation.GroupRouter

object GroupComponentHolder : ComponentHolder<GroupFeatureApi> {

	private var component: GroupFeatureApi? = null

	fun init(core: CoreApi, router: GroupRouter) {
		if (component == null) {
			component = DaggerGroupComponent.factory().create(core, router)
		}
	}

	override fun get(): GroupFeatureApi =
		checkNotNull(component) { "GroupComponent was not initialized" }

	override fun reset() {
		component = null
	}
}
