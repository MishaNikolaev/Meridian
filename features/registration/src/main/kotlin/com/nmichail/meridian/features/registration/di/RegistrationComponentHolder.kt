package com.nmichail.meridian.features.registration.di

import com.nmichail.meridian.core.di.ComponentHolder
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.registration.presentation.RegistrationRouter

object RegistrationComponentHolder : ComponentHolder<RegistrationFeatureApi> {

	private var component: RegistrationFeatureApi? = null

	fun init(core: CoreApi, router: RegistrationRouter) {
		if (component == null) {
			component = DaggerRegistrationComponent.factory().create(core, router)
		}
	}

	override fun get(): RegistrationFeatureApi =
		checkNotNull(component) { "RegistrationComponent was not initialized" }

	override fun reset() {
		component = null
	}
}
