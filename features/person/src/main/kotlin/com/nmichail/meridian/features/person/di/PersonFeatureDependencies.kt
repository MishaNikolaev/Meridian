package com.nmichail.meridian.features.person.di

import com.nmichail.meridian.core.di.BaseDependencyHolder
import com.nmichail.meridian.core.di.DependencyHolder0
import com.nmichail.meridian.core.di.FeatureDependencies
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.person.presentation.PersonRouter

interface PersonFeatureDependencies : FeatureDependencies {

	fun core(): CoreApi

	fun router(): PersonRouter
}

fun personFeatureDependencies(
	core: CoreApi,
	router: PersonRouter,
): PersonFeatureDependencies = object : DependencyHolder0<PersonFeatureDependencies>() {
	override val block: (BaseDependencyHolder<PersonFeatureDependencies>) -> PersonFeatureDependencies = { holder ->
		object : PersonFeatureDependencies {
			override fun core(): CoreApi = core

			override fun router(): PersonRouter = router

			override val dependencyHolder: BaseDependencyHolder<out FeatureDependencies> = holder
		}
	}
}.dependencies
