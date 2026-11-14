package com.nmichail.meridian.features.registration.di

import com.nmichail.meridian.core.di.ComponentHolder
import com.nmichail.meridian.core.di.ComponentHolderDelegate

object RegistrationComponentHolder :
	ComponentHolder<RegistrationFeatureApi, RegistrationFeatureDependencies> {

	private val componentHolderDelegate = ComponentHolderDelegate<
		RegistrationFeatureApi,
		RegistrationFeatureDependencies,
		RegistrationComponent,
		> { dependencies ->
		DaggerRegistrationComponent.factory().create(dependencies.core(), dependencies.router())
	}

	override var dependencyProvider: (() -> RegistrationFeatureDependencies)?
		by componentHolderDelegate::dependencyProvider

	override fun get(): RegistrationFeatureApi = componentHolderDelegate.get()
}
