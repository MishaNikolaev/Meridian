package com.nmichail.meridian.features.registration.di

import com.nmichail.meridian.core.di.ComponentHolder

object RegistrationComponentHolder : ComponentHolder<RegistrationFeatureApi> {

	private var component: RegistrationFeatureApi? = null

	fun init(dependencies: RegistrationFeatureDependencies) {
		if (component == null) {
			component = DaggerRegistrationComponent.factory().create(dependencies.core(), dependencies.router())
		}
	}

	override fun get(): RegistrationFeatureApi =
		checkNotNull(component) { "RegistrationComponent was not initialized" }

	override fun reset() {
		component = null
	}
}
