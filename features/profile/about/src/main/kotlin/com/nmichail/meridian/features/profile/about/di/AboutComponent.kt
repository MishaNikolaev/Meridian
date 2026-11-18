package com.nmichail.meridian.features.profile.about.di

import com.nmichail.meridian.core.di.PerFeature
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.core.presentation.di.ViewModelModule
import com.nmichail.meridian.features.profile.about.presentation.AboutRouter
import dagger.BindsInstance
import dagger.Component

@PerFeature
@Component(
	dependencies = [CoreApi::class],
	modules = [AboutModule::class, ViewModelModule::class],
)
internal interface AboutComponent : AboutFeatureApi {

	@Component.Factory
	interface Factory {

		fun create(core: CoreApi, @BindsInstance router: AboutRouter): AboutComponent
	}
}