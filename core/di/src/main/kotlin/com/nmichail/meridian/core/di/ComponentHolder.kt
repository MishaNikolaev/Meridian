package com.nmichail.meridian.core.di

interface ComponentHolder<C : FeatureApi, D : FeatureDependencies> {

	fun init(dependencies: D)

	fun get(): C

	fun reset()
}
