package com.nmichail.meridian.features.profile.busy.di

import com.nmichail.meridian.core.di.BaseDependencyHolder
import com.nmichail.meridian.core.di.DependencyHolder0
import com.nmichail.meridian.core.di.FeatureDependencies
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.profile.busy.presentation.BusyRouter

interface BusyFeatureDependencies : FeatureDependencies {

	fun core(): CoreApi

	fun router(): BusyRouter
}

fun busyFeatureDependencies(
	core: CoreApi,
	router: BusyRouter,
): BusyFeatureDependencies = object : DependencyHolder0<BusyFeatureDependencies>() {

	override val block: (BaseDependencyHolder<BusyFeatureDependencies>) -> BusyFeatureDependencies = { holder ->
		object : BusyFeatureDependencies {
			override fun core(): CoreApi = core

			override fun router(): BusyRouter = router

			override val dependencyHolder: BaseDependencyHolder<out FeatureDependencies> = holder
		}
	}
}.dependencies