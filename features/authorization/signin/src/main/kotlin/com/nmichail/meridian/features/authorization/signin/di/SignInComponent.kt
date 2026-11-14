package com.nmichail.meridian.features.authorization.signin.di

import com.nmichail.meridian.core.di.PerFeature
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.core.presentation.di.ViewModelModule
import com.nmichail.meridian.features.authorization.signin.presentation.SignInRouter
import dagger.BindsInstance
import dagger.Component

@PerFeature
@Component(
	dependencies = [CoreApi::class],
	modules = [SignInModule::class, ViewModelModule::class],
)
internal interface SignInComponent : SignInFeatureApi {

	@Component.Factory
	interface Factory {

		fun create(core: CoreApi, @BindsInstance router: SignInRouter): SignInComponent
	}
}
