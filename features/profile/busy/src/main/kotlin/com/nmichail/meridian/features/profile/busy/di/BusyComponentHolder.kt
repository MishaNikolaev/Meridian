package com.nmichail.meridian.features.profile.busy.di

import com.nmichail.meridian.core.di.ComponentHolder
import com.nmichail.meridian.core.di.ComponentHolderDelegate

object BusyComponentHolder : ComponentHolder<BusyFeatureApi, BusyFeatureDependencies> {

	private val componentHolderDelegate = ComponentHolderDelegate<
		BusyFeatureApi,
		BusyFeatureDependencies,
		BusyComponent,
		> { dependencies ->
		DaggerBusyComponent.factory().create(dependencies.core(), dependencies.router())
	}

	override var dependencyProvider: (() -> BusyFeatureDependencies)? by componentHolderDelegate::dependencyProvider

	override fun get(): BusyFeatureApi = componentHolderDelegate.get()
}