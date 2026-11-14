package com.nmichail.meridian.features.profile.di

import com.nmichail.meridian.core.di.PerFeature
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.core.presentation.di.ViewModelModule
import com.nmichail.meridian.features.profile.presentation.ProfileRouter
import dagger.BindsInstance
import dagger.Component

@PerFeature
@Component(
	dependencies = [CoreApi::class],
	modules = [ProfileModule::class, ViewModelModule::class],
)
internal interface ProfileComponent : ProfileFeatureApi {
	@Component.Factory
	interface Factory {
		fun create(core: CoreApi, @BindsInstance router: ProfileRouter): ProfileComponent
	}
}
