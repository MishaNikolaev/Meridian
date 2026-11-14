package com.nmichail.meridian.features.profile.di

import com.nmichail.meridian.core.di.ComponentHolder

object ProfileComponentHolder : ComponentHolder<ProfileFeatureApi> {
	private var component: ProfileComponent? = null

	fun init(dependencies: ProfileFeatureDependencies) {
		if (component == null) {
			component = DaggerProfileComponent.factory().create(dependencies.core(), dependencies.router())
		}
	}

	override fun get(): ProfileFeatureApi =
		checkNotNull(component) { "ProfileComponent was not initialized" }

	override fun reset() {
		component = null
	}
}
