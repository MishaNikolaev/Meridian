package com.nmichail.meridian.features.registration.di

import com.nmichail.meridian.core.di.BaseDependencyHolder
import com.nmichail.meridian.core.di.DependencyHolder0
import com.nmichail.meridian.core.di.FeatureDependencies
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.registration.presentation.RegistrationRouter

interface RegistrationFeatureDependencies : FeatureDependencies {

	fun core(): CoreApi

	fun router(): RegistrationRouter
}

fun registrationFeatureDependencies(
	core: CoreApi,
	router: RegistrationRouter,
): RegistrationFeatureDependencies = object : DependencyHolder0<RegistrationFeatureDependencies>() {
	override val block: (BaseDependencyHolder<RegistrationFeatureDependencies>) -> RegistrationFeatureDependencies =
		{ holder ->
			object : RegistrationFeatureDependencies {
				override fun core(): CoreApi = core

				override fun router(): RegistrationRouter = router

				override val dependencyHolder: BaseDependencyHolder<out FeatureDependencies> = holder
			}
		}
}.dependencies