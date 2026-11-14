package com.nmichail.meridian.features.groups.di

import com.nmichail.meridian.core.di.ComponentHolder
import com.nmichail.meridian.core.di.ComponentHolderDelegate

object GroupComponentHolder : ComponentHolder<GroupFeatureApi, GroupFeatureDependencies> {

	private val componentHolderDelegate = ComponentHolderDelegate<
		GroupFeatureApi,
		GroupFeatureDependencies,
		GroupComponent,
		> { dependencies ->
		DaggerGroupComponent.factory().create(
			dependencies.core(),
			dependencies.session(),
			dependencies.router(),
		)
	}

	override var dependencyProvider: (() -> GroupFeatureDependencies)? by componentHolderDelegate::dependencyProvider

	override fun get(): GroupFeatureApi = componentHolderDelegate.get()

	fun clear() {
		componentHolderDelegate.clear()
	}
}
