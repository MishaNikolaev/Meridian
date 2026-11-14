package com.nmichail.meridian.core.di

import java.lang.ref.WeakReference

class ComponentHolderDelegate<A : FeatureApi, D : FeatureDependencies, C : A>(
	private val componentFactory: (D) -> C,
) : ComponentHolder<A, D> {

	override var dependencyProvider: (() -> D)? = null

	private var componentWeakRef: WeakReference<C>? = null

	fun getComponentImpl(): C {
		var component: C? = null

		synchronized(this) {
			dependencyProvider?.let { dependencyProvider ->
				component = componentWeakRef?.get()
				if (component == null) {
					component = componentFactory(dependencyProvider())
					componentWeakRef = WeakReference(component)
				}
			} ?: throw IllegalStateException(
				"dependencyProvider for component with factory $componentFactory is not initialized",
			)
		}

		return component ?: throw IllegalStateException(
			"Component holder with component factory $componentFactory is not initialized",
		)
	}

	override fun get(): A = getComponentImpl()

	fun clear() {
		synchronized(this) {
			dependencyProvider = null
			componentWeakRef = null
		}
	}
}