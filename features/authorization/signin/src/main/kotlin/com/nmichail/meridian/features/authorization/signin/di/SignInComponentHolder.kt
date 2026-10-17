package com.nmichail.meridian.features.authorization.signin.di

import com.nmichail.meridian.core.di.ComponentHolder
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.authorization.signin.presentation.SignInRouter

object SignInComponentHolder : ComponentHolder<SignInFeatureApi> {

	private var component: SignInFeatureApi? = null

	fun init(core: CoreApi, router: SignInRouter) {
		if (component == null) {
			component = DaggerSignInComponent.factory().create(core, router)
		}
	}

	override fun get(): SignInFeatureApi =
		checkNotNull(component) { "SignInComponent was not initialized" }

	override fun reset() {
		component = null
	}
}
