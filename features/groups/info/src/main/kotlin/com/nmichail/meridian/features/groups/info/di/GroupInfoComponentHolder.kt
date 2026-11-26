package com.nmichail.meridian.features.groups.info.di

import com.nmichail.meridian.core.di.ComponentHolder
import com.nmichail.meridian.core.di.ComponentHolderDelegate

object GroupInfoComponentHolder : ComponentHolder<GroupInfoFeatureApi, GroupInfoFeatureDependencies> {

	private val componentHolderDelegate = ComponentHolderDelegate<
		GroupInfoFeatureApi,
		GroupInfoFeatureDependencies,
		GroupInfoComponent,
		> { dependencies ->
		DaggerGroupInfoComponent.factory().create(
			dependencies.core(),
			dependencies.session(),
			dependencies.groups(),
			dependencies.router(),
		)
	}

	override var dependencyProvider: (() -> GroupInfoFeatureDependencies)? by componentHolderDelegate::dependencyProvider

	override fun get(): GroupInfoFeatureApi = componentHolderDelegate.get()

	fun clear() {
		componentHolderDelegate.clear()
	}
}