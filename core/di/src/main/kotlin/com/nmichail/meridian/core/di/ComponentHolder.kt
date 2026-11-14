package com.nmichail.meridian.core.di

interface ComponentHolder<C : FeatureApi, D : FeatureDependencies> {

	var dependencyProvider: (() -> D)?

	fun get(): C
}