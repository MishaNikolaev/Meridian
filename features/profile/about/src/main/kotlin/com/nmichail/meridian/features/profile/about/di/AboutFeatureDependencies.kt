package com.nmichail.meridian.features.profile.about.di

import com.nmichail.meridian.core.di.BaseDependencyHolder
import com.nmichail.meridian.core.di.DependencyHolder0
import com.nmichail.meridian.core.di.FeatureDependencies
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.profile.about.presentation.AboutRouter

interface AboutFeatureDependencies : FeatureDependencies {

	fun core(): CoreApi

	fun router(): AboutRouter
}

fun aboutFeatureDependencies(
	core: CoreApi,
	router: AboutRouter,
): AboutFeatureDependencies = object : DependencyHolder0<AboutFeatureDependencies>() {

	override val block: (BaseDependencyHolder<AboutFeatureDependencies>) -> AboutFeatureDependencies = { holder ->
		object : AboutFeatureDependencies {
			override fun core(): CoreApi = core

			override fun router(): AboutRouter = router

			override val dependencyHolder: BaseDependencyHolder<out FeatureDependencies> = holder
		}
	}
}.dependencies