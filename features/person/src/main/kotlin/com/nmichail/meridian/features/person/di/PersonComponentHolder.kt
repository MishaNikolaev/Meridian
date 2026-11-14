package com.nmichail.meridian.features.person.di

import com.nmichail.meridian.core.di.ComponentHolder

object PersonComponentHolder : ComponentHolder<PersonFeatureApi> {

	private var component: PersonFeatureApi? = null

	fun init(dependencies: PersonFeatureDependencies) {
		if (component == null) {
			component = DaggerPersonComponent.factory().create(dependencies.core(), dependencies.router())
		}
	}

	override fun get(): PersonFeatureApi =
		checkNotNull(component) { "PersonComponent was not initialized" }

	override fun reset() {
		component = null
	}
}
