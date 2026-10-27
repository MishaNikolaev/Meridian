package com.nmichail.meridian.features.authorization.splash.di

import com.nmichail.meridian.core.di.PerFeature
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.core.presentation.di.ViewModelModule
import com.nmichail.meridian.features.authorization.splash.presentation.SplashRouter
import dagger.BindsInstance
import dagger.Component

@PerFeature
@Component(
	dependencies = [CoreApi::class],
	modules = [SplashModule::class, ViewModelModule::class],
)
interface SplashComponent : SplashFeatureApi {

	@Component.Factory
	interface Factory {

		fun create(core: CoreApi, @BindsInstance router: SplashRouter): SplashComponent
	}
}