package com.nmichail.meridian.core.di

interface BaseDependencyHolder<D : FeatureDependencies> {

	val dependencies: D
}

abstract class DependencyHolder0<D : FeatureDependencies> : BaseDependencyHolder<D> {

	abstract val block: (BaseDependencyHolder<D>) -> D

	override val dependencies: D
		get() = block(this)
}

abstract class DependencyHolder1<A1 : FeatureApi, D : FeatureDependencies>(
	private val api1: A1,
) : BaseDependencyHolder<D> {

	abstract val block: (BaseDependencyHolder<D>, A1) -> D

	override val dependencies: D
		get() = block(this, api1)
}

abstract class DependencyHolder2<A1 : FeatureApi, A2 : FeatureApi, D : FeatureDependencies>(
	private val api1: A1,
	private val api2: A2,
) : BaseDependencyHolder<D> {

	abstract val block: (BaseDependencyHolder<D>, A1, A2) -> D

	override val dependencies: D
		get() = block(this, api1, api2)
}

abstract class DependencyHolder3<A1 : FeatureApi, A2 : FeatureApi, A3 : FeatureApi, D : FeatureDependencies>(
	private val api1: A1,
	private val api2: A2,
	private val api3: A3,
) : BaseDependencyHolder<D> {

	abstract val block: (BaseDependencyHolder<D>, A1, A2, A3) -> D

	override val dependencies: D
		get() = block(this, api1, api2, api3)
}