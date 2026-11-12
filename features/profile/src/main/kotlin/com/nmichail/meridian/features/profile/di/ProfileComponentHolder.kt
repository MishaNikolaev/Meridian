package com.nmichail.meridian.features.profile.di

import com.nmichail.meridian.core.di.ComponentHolder
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.profile.presentation.ProfileRouter

object ProfileComponentHolder : ComponentHolder<ProfileFeatureApi> {
	private var component: ProfileComponent? = null

	fun init(core: CoreApi, router: ProfileRouter) {
		if (component == null) {
			component = DaggerProfileComponent.factory().create(core, router)
		}
	}

	override fun get(): ProfileFeatureApi =
		checkNotNull(component) { "ProfileComponent was not initialized" }

	override fun reset() {
		component = null
	}
}
