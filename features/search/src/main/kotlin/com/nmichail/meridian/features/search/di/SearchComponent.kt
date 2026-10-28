package com.nmichail.meridian.features.search.di

import com.nmichail.meridian.core.di.PerFeature
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.core.presentation.di.ViewModelModule
import com.nmichail.meridian.features.search.presentation.SearchRouter
import dagger.BindsInstance
import dagger.Component

@PerFeature
@Component(
	dependencies = [CoreApi::class],
	modules = [SearchFeatureModule::class, ViewModelModule::class],
)
interface SearchComponent : SearchFeatureApi {

	@Component.Factory
	interface Factory {

		fun create(core: CoreApi, @BindsInstance router: SearchRouter): SearchComponent
	}
}
