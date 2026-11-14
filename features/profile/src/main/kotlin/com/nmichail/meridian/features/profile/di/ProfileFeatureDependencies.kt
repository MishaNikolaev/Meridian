package com.nmichail.meridian.features.profile.di

import com.nmichail.meridian.core.di.BaseDependencyHolder
import com.nmichail.meridian.core.di.DependencyHolder0
import com.nmichail.meridian.core.di.FeatureDependencies
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.profile.presentation.ProfileRouter

interface ProfileFeatureDependencies : FeatureDependencies {

	fun core(): CoreApi

	fun router(): ProfileRouter
}

fun profileFeatureDependencies(
	core: CoreApi,
	router: ProfileRouter,
): ProfileFeatureDependencies = object : DependencyHolder0<ProfileFeatureDependencies>() {
	override val block: (BaseDependencyHolder<ProfileFeatureDependencies>) -> ProfileFeatureDependencies = { holder ->
		object : ProfileFeatureDependencies {
			override fun core(): CoreApi = core

			override fun router(): ProfileRouter = router

			override val dependencyHolder: BaseDependencyHolder<out FeatureDependencies> = holder
		}
	}
}.dependencies
