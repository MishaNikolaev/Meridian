package com.nmichail.meridian.features.calls.di

import com.nmichail.meridian.core.di.ComponentHolder
import com.nmichail.meridian.core.di.ComponentHolderDelegate

object CallsComponentHolder : ComponentHolder<CallsFeatureApi, CallsFeatureDependencies> {

	private val componentHolderDelegate = ComponentHolderDelegate<
		CallsFeatureApi,
		CallsFeatureDependencies,
		CallsComponent,
		> { dependencies ->
		DaggerCallsComponent.factory().create(
			dependencies.core(),
			dependencies.session(),
			dependencies.phone(),
			dependencies.signal(),
		)
	}

	override var dependencyProvider: (() -> CallsFeatureDependencies)? by componentHolderDelegate::dependencyProvider

	override fun get(): CallsFeatureApi = componentHolderDelegate.get()

	fun clear() {
		componentHolderDelegate.clear()
	}
}