package com.nmichail.meridian.features.person.di

import com.nmichail.meridian.core.di.ComponentHolder
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.person.presentation.PersonRouter

object PersonComponentHolder : ComponentHolder<PersonFeatureApi> {

	private var component: PersonFeatureApi? = null

	fun init(core: CoreApi, router: PersonRouter) {
		if (component == null) {
			component = DaggerPersonComponent.factory().create(core, router)
		}
	}

	override fun get(): PersonFeatureApi =
		checkNotNull(component) { "PersonComponent was not initialized" }

	override fun reset() {
		component = null
	}
}
