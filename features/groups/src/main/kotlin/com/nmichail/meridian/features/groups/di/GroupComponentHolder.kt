package com.nmichail.meridian.features.groups.di

import com.nmichail.meridian.core.di.ComponentHolder

object GroupComponentHolder : ComponentHolder<GroupFeatureApi> {

	private var component: GroupFeatureApi? = null

	fun init(dependencies: GroupFeatureDependencies) {
		check(component == null) { "GroupComponent is already initialized" }
		component = DaggerGroupComponent.factory().create(
			dependencies.core(),
			dependencies.session(),
			dependencies.router(),
		)
	}

	fun isInitialized(): Boolean = component != null

	override fun get(): GroupFeatureApi =
		checkNotNull(component) { "GroupComponent was not initialized" }

	override fun reset() {
		component = null
	}
}
