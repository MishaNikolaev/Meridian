package com.nmichail.meridian.features.home.di

import com.nmichail.meridian.core.di.PerFeature
import com.nmichail.meridian.core.presentation.di.ViewModelModule
import dagger.Component

@PerFeature
@Component(
	dependencies = [HomeDependencies::class],
	modules = [HomeModule::class, ViewModelModule::class],
)
interface HomeComponent : HomeFeatureApi {

	@Component.Factory
	interface Factory {

		fun create(dependencies: HomeDependencies): HomeComponent
	}
}
